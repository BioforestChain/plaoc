use bytes::Bytes;
use futures::Future;
use std::sync::mpsc::{channel, Sender};
use std::task::Poll;
use std::time::Duration;
use std::{
    pin::Pin,
    sync::{Arc, Mutex},
    task::{Context, Waker},
    thread::{self, JoinHandle},
};

/// 运行的任务类型
#[derive(Clone, Copy)]
#[allow(dead_code)]
enum PromiseType {
    /// 等待
    PENDING,
    ///  完成
    FULFILLED, // 拒绝
               // REJECTED,
}

#[derive(Clone)]
pub struct BufferInstance {
    // waitter: Option<PromiseOut<'a, Vec<u8>, ()>>,
    // waitter: Arc<Mutex<Box<PromiseOut>>>,
    pub cache: Vec<Bytes>,
    pub full: bool,
    pub current_height: usize,
    pub water_threshold: usize, // 8MB 1024 * 1024 * 8
}

pub struct PromiseOut {
    handle: Option<JoinHandle<()>>,
    dispatcher: Sender<Event>,
    mux: Vec<Waker>,
    status: PromiseType,
    pub result: Option<Bytes>,
}

enum Event {
    Pending,
    BufferArray(Bytes),
}

impl BufferInstance {
    pub fn new() -> Self {
        // let promise_out = PromiseOut::new();
        BufferInstance {
            // waitter: promise_out,
            full: false,
            cache: vec![Bytes::new()],
            current_height: 0,
            water_threshold: 1024 * 1024 * 8,
        }
    }
    pub fn push(&mut self, buffer_array: Bytes) -> Result<bool, String> {
        log::info!(" BufferInstancepush 👾 ==> :{:?}", &buffer_array);
        if self.full {
            return Err("request full ".to_string());
        }
        self.current_height += buffer_array.len();
        self.cache.push(buffer_array);
        return Result::Ok(self.over_flow());
    }
    pub fn shift(&mut self) -> Bytes {
        // log::info!(" BufferInstanceshift 👾 ==> :{:?}", self.cache);
        if self.cache.is_empty() {
           return Bytes::new()
        }
        let vec = self.cache.remove(0);
        self.current_height -= vec.len();
        return vec;
    }
    /// 如果当前水位已经超过阈值，返回true 已经阻塞
    pub fn over_flow(&mut self) -> bool {
        self.full = self.current_height >= self.water_threshold;
        self.full
    }
    // /// 初始化等待者
    // pub fn init_waitter(&mut self) -> Arc<Mutex<Box<PromiseOut>>> {
    //     let waitter = self.waitter.lock();
    //     match waitter {
    //         Ok(_) => self.waitter.clone(),
    //         Err(_) => {
    //             // self.waitter = PromiseOut::new();
    //             // self.waitter
    //             PromiseOut::new()
    //         }
    //     }
    // }
    // /// 是否已经存在等待者
    // pub fn has_waitter(&self) -> bool {
    //     let waitter = self.waitter.lock();
    //     match waitter {
    //         Ok(_) => true,
    //         Err(_) => false,
    //     }
    // }
    // /// 等待者完成了
    // pub fn resolve_waitter(&mut self, data: Bytes) {
    //     // let data: &'a Vec<u8> = Box::leak(Box::new(scanner_data));
    //     log::info!(" BufferInstancere solve_waitter 👾 ==> :{:?}", &data);
    //     self.waitter.lock().unwrap().resolve(data);
    //     // self.waitter = None;
    // }
}

// impl Future for BufferInstance {
//     type Output = Bytes;
//     fn poll(self: Pin<&mut Self>, cx: &mut Context<'_>) -> Poll<Self::Output> {
//         let mut waitter = self.waitter.lock().unwrap();
//         if let PromiseType::PENDING = waitter.status {
//             // 存下来，等待下次任务调用 🤓
//             // waitter.register(self.shift(), cx.waker().clone());
//             waitter.mux.push(cx.waker().clone());
//             log::info!(" PromiseOut Pending 😴");
//             Poll::Pending
//         } else {
//             // 完成✅
//             waitter.status = PromiseType::FULFILLED;
//             let res = waitter.result.clone();
//             log::info!(" PromiseOut Ready 😲 ");
//             match res {
//                 Some(buffer) => Poll::Ready(buffer),
//                 None => Poll::Ready(Bytes::new()),
//             }
//         }
//     }
// }

// impl Future for PromiseOut {
//     type Output = Vec<u8>;

//     fn poll(mut self: Pin<&mut Self>, cx: &mut Context<'_>) -> std::task::Poll<Self::Output> {
//         if let PromiseType::PENDING = self.status {
//             self.mux.push(cx.waker().clone());
//             log::info!(" PromiseOut Pending 😴");
//             // cx.waker().wake();
//             Poll::Pending
//         } else {
//             let res = self.result.clone();
//             log::info!(" PromiseOut Ready 😲 ");
//             match res {
//                 Some(buffer) => Poll::Ready(buffer),
//                 None => Poll::Ready(vec![0]),
//             }
//         }
//     }
// }

impl PromiseOut {
    fn new() -> Arc<Mutex<Box<PromiseOut>>> {
        let (tx, rx) = channel::<Event>();
        let promise_out = Arc::new(Mutex::new(Box::new(PromiseOut {
            mux: vec![],
            result: None,
            status: PromiseType::PENDING,
            handle: None,
            dispatcher: tx,
        })));
        let promise_out_clone = Arc::downgrade(&promise_out);
        let handle = thread::spawn(move || {
            let mut handles = vec![];
            // 执行
            for obj in rx {
                let reactor = promise_out_clone.clone();
                match obj {
                    Event::Pending => break,
                    Event::BufferArray(buffer_array) => {
                        let event_handle = thread::spawn(move || {
                            let reactor = reactor.upgrade().unwrap();
                            reactor.lock().map(|mut r| r.resolve(buffer_array)).unwrap();
                        });
                        handles.push(event_handle);
                    }
                }
            }
            // join
            handles
                .into_iter()
                .for_each(|handle| handle.join().unwrap());
        });
        promise_out
            .lock()
            .map(|mut r| r.handle = Some(handle))
            .unwrap();
        promise_out
    }
    /// 等到数据了
    pub fn resolve(&mut self, value: Bytes) {
        self.result = Some(value);
        self.status = PromiseType::FULFILLED;
        self.wake();
    }

    pub async fn getData() {}

    pub fn register(&mut self, buffer_array: Bytes, waker: Waker) {
        self.mux.push(waker);
        self.dispatcher
            .send(Event::BufferArray(buffer_array))
            .unwrap();
    }
    // 没等到
    // #[allow(dead_code)]
    // pub fn reject(&mut self, error: &'a E) {
    //     self.result = Some(Result::Err(error));
    //     self.status = PromiseType::REJECTED;
    //     self._wake();
    // }
    fn wake(&mut self) {
        for waker in self.mux.iter() {
            waker.clone().wake()
        }
        self.mux.clear()
    }
}

impl Drop for PromiseOut {
    fn drop(&mut self) {
        self.dispatcher.send(Event::Pending).unwrap();
        self.handle.take().map(|h| h.join().unwrap()).unwrap();
    }
}
