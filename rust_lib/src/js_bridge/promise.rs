use futures::task::Poll;
use futures::Future;
use std::marker::Send;
use std::result::Result::Ok;
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

pub struct BufferInstance {
    pub waitter: Arc<Mutex<PromiseOut>>,
    // pub PromiseOut: Mutex<Option<BoxFuture<'static, ()>>>,
    pub cache: Vec<Vec<u8>>,
    pub currentHeight: i32,
    pub waterThrotth: i32, // 8MB
}

impl BufferInstance {
    pub fn new() -> Self {
        let promise_out = Arc::new(Mutex::new(PromiseOut {
            waker: None,
            status: PromiseType::PENDING,
            promise: None,
        }));
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
            cache: vec![vec![]],
            currentHeight: 0,
            waterThrotth: 1024 * 1024 * 8,
        }
    }
}

impl Future for BufferInstance {
    type Output = Result<Vec<u8>, &'static str>;

    fn poll(
         self: std::pin::Pin<&mut Self>,
        cx: &mut std::task::Context<'_>,
    ) -> std::task::Poll<Self::Output> {
        let mut shared_state = self.waitter.lock().unwrap();
        match shared_state.status {
            PromiseType::PENDING => {
                cx.waker().to_owned();
                Poll::Pending
            }
            PromiseType::FULFILLED => {
                let res = self.cache.remove(0).clone();
                Poll::Ready(Ok(res))
            }
            PromiseType::REJECTED => Poll::Ready(Err("promise Error")),
        }
    }
}

pub struct PromiseOut {
    pub waker: Option<Waker>,
    pub promise: Option<Promise<u8, &'static str>>,
    pub status: PromiseType,
}

impl PromiseOut {
    fn new() -> Self {
        Self {
            promise: None,
            waker: None,
            status: PromiseType::PENDING,
        }
    }
}

pub struct Promise<T: Send, E: Send> {
    receiver: Receiver<Result<T, E>>,
}

impl<T: Send + 'static, E: Send + 'static> Promise<T, E> {
    pub fn new<F>(func: F) -> Promise<T, E>
    where
        F: FnOnce() -> Result<T, E>,
        F: Send + 'static,
    {
        let (tx, rx) = channel();

        thread::spawn(move || {
            Promise::impl_new(tx, func);
        });

        Promise { receiver: rx }
    }

    pub fn resolve(val: T) -> Promise<T, E> {
        Promise::from_result(Ok(val))
    }

    pub fn reject(val: E) -> Promise<T, E> {
        Promise::from_result(Err(val))
    }
    pub fn then<T2, E2, F1, F2>(self, callback: F1, errback: F2) -> Promise<T2, E2>
    where
        T2: Send + 'static,
        E2: Send + 'static,
        F1: FnOnce(T) -> Result<T2, E2>,
        F2: FnOnce(E) -> Result<T2, E2>,
        F1: Send + 'static,
        F2: Send + 'static,
    {
        let recv = self.receiver;
        let (tx, rx) = channel();

        thread::spawn(move || {
            Promise::impl_then(tx, recv, callback, errback);
        });

        Promise { receiver: rx }
    }

    pub fn then_result<T2, E2, F>(self, callback: F) -> Promise<T2, E2>
    where
        T2: Send + 'static,
        E2: Send + 'static,
        F: FnOnce(Result<T, E>) -> Result<T2, E2>,
        F: Send + 'static,
    {
        let recv = self.receiver;
        let (tx, rx) = channel();

        thread::spawn(move || {
            Promise::impl_then_result(tx, recv, callback);
        });

        Promise { receiver: rx }
    }

    pub fn ok_then<T2, F>(self, callback: F) -> Promise<T2, E>
    where
        T2: Send + 'static,
        F: Send + 'static,
        F: FnOnce(T) -> Result<T2, E>,
    {
        let recv = self.receiver;
        let (tx, rx) = channel();

        thread::spawn(move || {
            Promise::impl_ok_then(tx, recv, callback);
        });

        Promise { receiver: rx }
    }

    pub fn catch<E2, F>(self, errback: F) -> Promise<T, E2>
    where
        F: FnOnce(E) -> Result<T, E2>,
        F: Send + 'static,
        E2: Send + 'static,
    {
        let recv = self.receiver;
        let (tx, rx) = channel();

        thread::spawn(move || {
            Promise::impl_err_then(tx, recv, errback);
        });

        Promise { receiver: rx }
    }

    pub fn from_result(result: Result<T, E>) -> Promise<T, E> {
        let (tx, rx) = channel();
        tx.send(result).unwrap();

        Promise { receiver: rx }
    }

    fn impl_new<F>(tx: Sender<Result<T, E>>, func: F)
    where
        F: FnOnce() -> Result<T, E>,
        F: Send + 'static,
    {
        let result = func();
        tx.send(result).unwrap_or(());
    }

    fn impl_then<T2, E2, F1, F2>(
        tx: Sender<Result<T2, E2>>,
        rx: Receiver<Result<T, E>>,
        callback: F1,
        errback: F2,
    ) where
        T2: Send + 'static,
        E2: Send + 'static,
        F1: FnOnce(T) -> Result<T2, E2>,
        F2: FnOnce(E) -> Result<T2, E2>,
        F1: Send + 'static,
        F2: Send + 'static,
    {
        if let Ok(message) = rx.recv() {
            match message {
                Ok(val) => tx.send(callback(val)).unwrap_or(()),
                Err(err) => tx.send(errback(err)).unwrap_or(()),
            };
        }
    }

    fn impl_then_result<T2, E2, F>(
        tx: Sender<Result<T2, E2>>,
        rx: Receiver<Result<T, E>>,
        callback: F,
    ) where
        T2: Send + 'static,
        E2: Send + 'static,
        F: FnOnce(Result<T, E>) -> Result<T2, E2>,
        F: Send + 'static,
    {
        if let Ok(result) = rx.recv() {
            tx.send(callback(result)).unwrap_or(());
        }
    }

    fn impl_ok_then<T2, F>(tx: Sender<Result<T2, E>>, rx: Receiver<Result<T, E>>, callback: F)
    where
        F: FnOnce(T) -> Result<T2, E>,
        F: Send + 'static,
        T2: Send + 'static,
    {
        if let Ok(message) = rx.recv() {
            match message {
                Ok(val) => tx.send(callback(val)).unwrap_or(()),
                Err(err) => tx.send(Err(err)).unwrap_or(()),
            }
        }
    }

    fn impl_err_then<E2, F>(tx: Sender<Result<T, E2>>, rx: Receiver<Result<T, E>>, errback: F)
    where
        F: FnOnce(E) -> Result<T, E2>,
        F: Send + 'static,
        E2: Send + 'static,
    {
        if let Ok(message) = rx.recv() {
            match message {
                Ok(val) => tx.send(Ok(val)).unwrap_or(()),
                Err(err) => tx.send(errback(err)).unwrap_or(()),
            }
        }
    }
}
