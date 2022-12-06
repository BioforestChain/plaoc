use deno_core::ZeroCopyBuf;
use futures::Future;
use std::collections::HashMap;
use std::sync::mpsc::{channel, Sender};
use std::task::Poll;
// use std::time::Duration;
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
    pub waitter: Arc<Mutex<Box<PromiseOut>>>,
    pub cache: Vec<ZeroCopyBuf>,
    pub full: bool,
    pub current_height: usize,
    pub water_threshold: usize, // 8MB 1024 * 1024 * 8
}
#[derive(Clone)]
pub struct PromiseOut {
    handle: Arc<Mutex<Box<Option<JoinHandle<()>>>>>,
    dispatcher: Sender<Event>,
    mux: Vec<Waker>,
    status: PromiseType,
    pub result: Option<ZeroCopyBuf>,
}

#[allow(dead_code)]
enum Event {
    Pending,
    BufferArray(ZeroCopyBuf),
}

impl BufferInstance {
    pub fn new() -> Self {
        let promise_out = PromiseOut::new();
        BufferInstance {
            waitter: promise_out,
            full: false,
            cache: vec![ZeroCopyBuf::new_temp(vec![0])],
            current_height: 0,
            water_threshold: 1024 * 1024 * 8,
        }
    }
    pub fn push(&mut self, buffer_array: ZeroCopyBuf) -> Result<bool, String> {
        log::info!(" BufferInstancepush 👾 ==> :{:?}", &buffer_array);
        if self.full {
            return Err("request full ".to_string());
        }
        self.current_height += buffer_array.len();
        self.cache.push(buffer_array);
        return Result::Ok(self.over_flow());
    }
    pub fn shift(&mut self) -> ZeroCopyBuf {
        // log::info!(" BufferInstanceshift 👾 ==> :{:?}", self.cache);
        if self.cache.is_empty() {
            return ZeroCopyBuf::new_temp(vec![0]);
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
    // /// 获取等待者
    // pub fn get_waitter(&mut self) -> Arc<Mutex<Box<PromiseOut>>> {
    //     let waitter = self.waitter.lock();
    //     match waitter {
    //         Ok(_) => self.waitter,
    //         Err(_) => {
    //             // *self.waitter = PromiseOut::new();
    //             self.waitter
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
    // // / 等待者完成了
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

impl Future for PromiseOut {
    type Output = Vec<u8>;

    fn poll(mut self: Pin<&mut Self>, cx: &mut Context<'_>) -> std::task::Poll<Self::Output> {
        if let PromiseType::PENDING = self.status {
            self.mux.push(cx.waker().clone());
            log::info!(" PromiseOut Pending 😴");
            // cx.waker().wake();
            Poll::Pending
        } else {
            let res = self.result.clone();
            log::info!(" PromiseOut Ready 😲 ");
            match res {
                Some(buffer) => Poll::Ready(buffer.to_vec()),
                None => Poll::Ready(vec![0]),
            }
        }
    }
}

impl PromiseOut {
    fn new() -> Arc<Mutex<Box<PromiseOut>>> {
        let (tx, rx) = channel::<Event>();
        let promise_out = Arc::new(Mutex::new(Box::new(PromiseOut {
            mux: vec![],
            result: None,
            status: PromiseType::PENDING,
            handle: Arc::new(Mutex::new(Box::new(None))),
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
            .map(|mut r| r.handle = Arc::new(Mutex::new(Box::new(Some(handle)))))
            .unwrap();
        promise_out
    }
    /// 等到数据了
    pub fn resolve(&mut self, value: ZeroCopyBuf) {
        self.result = Some(value);
        self.status = PromiseType::FULFILLED;
        self.wake();
    }

    // pub fn register(&mut self, buffer_array: Bytes, waker: Waker) {
    //     self.mux.push(waker);
    //     self.dispatcher
    //         .send(Event::BufferArray(buffer_array))
    //         .unwrap();
    // }
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
        self.handle
            .lock()
            .unwrap()
            .take()
            .map(|h| h.join().unwrap())
            .unwrap();
    }
}

type HeadView = String;

pub struct BufferTask {
    pub data: HashMap<HeadView, ZeroCopyBuf>,
    // buffer: Option<ZeroCopyBuf> ,
    // waker: Arc<Mutex<Option<Waker>>>,
}

// impl Future for BufferTask {
//     type Output = Bytes;

//     fn poll(self: Pin<&mut Self>, cx: &mut Context<'_>) -> Poll<Self::Output> {
//         match self.buffer.clone() {
//             Some(byte)=> {
//                 log::info!(" BufferTask 🥳 future head_view ready");
//                 Poll::Ready(byte)
//             }
//             None => {
//                 let mut waker = self.waker.lock().unwrap();
//                 log::info!(" BufferTask 🥳 future head_view pending");
//                 *waker = Some(cx.waker().clone());
//                 Poll::Pending
//             }
//         }
//     }
// }

impl BufferTask {
    pub fn new() -> Self {
        Self {
            // waker:Arc::new(Mutex::new(None)),
            // buffer: None,
            data: HashMap::new(),
        }
    }
    pub fn insert(&mut self, head_view: String, buffer: ZeroCopyBuf) -> Result<ZeroCopyBuf, ()> {
        self.data.insert(head_view.clone(), buffer);
        let res = self.data.get(&head_view);
        // log::info!(" BufferTaskxx 🥳 insert res:{:?}",res);
        match res {
            Some(byte) => {
                return Ok(byte.clone());
            }
            None => {
                return Err(());
            }
        }
    }
    pub fn get(&mut self, head_view: String) -> ZeroCopyBuf {
        let data = self.data.get(&head_view);
        // thread::sleep(Duration::from_micros(500)); // 微秒
        // log::info!("获取数据 🤖：{:?},headView:{:?}",data,head_view);
        match data {
            Some(byte) => {
                // log::info!(" BufferTask 🥳 get Some {:?}",byte);
                return byte.clone();
            }
            None => {
                // log::info!(" BufferTask 🥵 get None");
                return ZeroCopyBuf::new_temp(vec![0]);
            }
        };
    }

    // pub fn resolve(&mut self,buffer:ZeroCopyBuf) {
    //     self.buffer = Some(buffer);
    //      // 创建新线程
    //      let thread_shared_state = self.waker.clone();
    //     thread::spawn(move || {
    //         let mut shared_state = thread_shared_state.lock().unwrap();
    //         // 通知执行器已经完成，可以继续`poll`对应的`Future`了
    //         if let Some(waker) = shared_state.take() {
    //             waker.wake()
    //         }

    //     });
    // }
}
