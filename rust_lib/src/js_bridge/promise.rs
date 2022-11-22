use deno_core::anyhow::Ok;
use futures::future::maybe_done;
use futures::task::Poll;
use futures::Future;
use std::borrow::Borrow;
use std::marker::Send;
use std::rc::Rc;
use std::sync::mpsc::channel;
use std::sync::mpsc::{Receiver, Sender};
use std::sync::{Arc, Mutex};
use std::task::Waker;
use std::thread;

/// 运行的任务类型
#[derive(Clone, Copy)]
#[allow(dead_code)]
pub enum PromiseType {
    /// 等待
    PENDING,
    ///  完成
    FULFILLED,
    /// 拒绝
    REJECTED,
}

// #[derive(Clone)]
pub struct BufferInstance<'a> {
    waitter: Option<PromiseOut<'a, Vec<u8>, ()>>,
    pub cache: Vec<Vec<u8>>,
    pub full: bool,
    pub current_height: usize,
    pub water_threshold: usize, // 8MB 1024 * 1024 * 8
}

impl<'a> BufferInstance<'a> {
    pub fn new() -> Self {
        let promise_out = None;
        // thread::spawn(move || {
        //     let mut shared_state = promise_out.lock().unwrap();
        //     // 通知执行器定时器已经完成，可以继续`poll`对应的`Future`了
        //     shared_state.status = PromiseType::FULFILLED;
        //     // if let Some(waker) = shared_state.waker.take() {
        //     //     waker.wake()
        //     // }
        // });
        BufferInstance {
            waitter: promise_out,
            full: false,
            cache: vec![vec![]],
            current_height: 0,
            water_threshold: 1024 * 1024 * 8,
        }
    }
    pub fn push(&mut self, buffer_array: Vec<u8>) -> Result<bool, String> {
        if self.full {
            return Err("request full ".to_string());
        }
        self.current_height += buffer_array.len();
        self.cache.push(buffer_array);
        return Result::Ok(self.over_flow());
    }
    pub fn shift(&mut self) -> Vec<u8> {
        let vec = self.cache.remove(0);
        self.current_height -= vec.len();
        return vec;
    }
    pub fn over_flow(&mut self) -> bool {
        self.full = self.current_height >= self.water_threshold;
        self.full
    }

    pub fn init_waitter(&mut self) -> Result<&PromiseOut<Vec<u8>, ()>, String> {
        match &self.waitter {
            Some(_) => Err("already exist waitter".to_string()),
            None => {
                self.waitter = Some(PromiseOut::new());
                Result::Ok(&self.waitter.as_ref().unwrap())
            }
        }
    }
    pub fn has_waitter(&self) -> bool {
        match self.waitter {
            Some(_) => true,
            None => false,
        }
    }
    pub fn resolve_waitter(&mut self, data: &'a Vec<u8>) {
        // let data: &'a Vec<u8> = Box::leak(Box::new(scanner_data));
        self.waitter.as_mut().unwrap().resolve(data);
        self.waitter = None;
    }
}

#[derive(Clone)]
pub struct PromiseOut<'a, T, E> {
    //  waker:   Option< &'static dyn FnOnce() -> ()>,
    // waker: Option< &'a Waker >,
    mux: Vec<Waker>,
    pub status: PromiseType,
    result: Option<Result<&'a T, &'a E>>,
}

impl<'a, T, E> Future for PromiseOut<'a, T, E> {
    type Output = Result<&'a T, &'a E>;

    fn poll(
        mut self: std::pin::Pin<&mut Self>,
        cx: &mut std::task::Context<'_>,
    ) -> std::task::Poll<Self::Output> {
        if let PromiseType::PENDING = self.status {
            self.mux.push(cx.waker().clone());
            Poll::Pending
        } else {
            let res = self.result.unwrap();
            Poll::Ready(res)
        }
    }
}

impl<'a, T, E> PromiseOut<'a, T, E> {
    fn new() -> PromiseOut<'a, T, E> {
        Self {
            // waker: None,
            mux: vec![],
            result: None,
            status: PromiseType::PENDING,
        }
    }

    pub fn resolve(&mut self, value: &'a T) {
        self.result = Some(Result::Ok(value));
        self.status = PromiseType::FULFILLED;
        self._wake();
    }
    pub fn reject(&mut self, error: &'a E) {
        self.result = Some(Result::Err(error));
        self.status = PromiseType::REJECTED;
        self._wake();
    }
    fn _wake(&mut self) {
        for waker in self.mux.iter() {
            waker.clone().wake()
        }
        self.mux.clear()
    }
}
