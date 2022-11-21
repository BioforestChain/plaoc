// #[cfg(target_os = "android")]
use crate::android::android_inter;
use crate::js_bridge::call_android_function;
use android_logger::Config;
use core::result::Result::Ok;
use deno_core::error::{custom_error, AnyError};
use deno_core::parking_lot::Mutex;
use deno_core::{op, ZeroCopyBuf};
use lazy_static::*;
use log::{debug, error, info, Level};
use std::thread;
use std::{collections::HashMap, str};

use super::call_android_function::call_java_open_back_pressure;
use super::promise::{BufferInstance, PromiseType};

// 添加一个全局变量来缓存信息，让js每次来拿，防止js内存爆炸,这里应该用channelId来定位每条消息，而不是用现在的队列
lazy_static! {
    // 消息通道
    pub(crate) static ref BUFFER_NOTIFICATION: Mutex<Vec<Vec<u8>>> = Mutex::new(vec![]);
    pub(crate) static ref BUFFER_RESOLVE: Mutex<Vec<Vec<u8>>> = Mutex::new(vec![]);
    // 系统操作通道
    pub(crate) static ref BUFFER_SYSTEM: Mutex<Vec<Vec<u8>>> = Mutex::new(vec![]);
}

type ChannelId = String;

pub const BUFFER_INSTANCES_MAP: Mutex<HashMap<ChannelId, BufferInstance>> =
    Mutex::new(HashMap::new());

/// deno-js消息从这里走到移动端
#[op]
pub fn op_js_to_rust_buffer(buffer: ZeroCopyBuf) {
    android_logger::init_once(
        Config::default()
            .with_min_level(Level::Debug)
            .with_tag("deno_runtime::rust_to_js_buffer"),
    );
    log::info!("i am op_js_to_rust_buffer {:?}", buffer);
    call_android_function::call_android(buffer.to_vec()); // 通知FFI函数
}

/// deno-js通过移动端的evalJs，把数据传递到dwebview-js
#[op]
pub fn op_eval_js(buffer: ZeroCopyBuf) {
    call_android_function::call_android_evaljs(buffer.to_vec()); // 通知FFI函数
}

///  deno-js 轮询访问这个方法，以达到把rust数据传递到deno-js的过程，这里负责的是移动端系统API的数据
#[op]
pub fn op_rust_to_js_system_buffer(channelId: String) -> Result<Vec<u8>, AnyError> {
    let buffer = BufferInstance::new();
    if !buffer.cache.is_empty() {
        let result = buffer.cache.remove(0); // like javascript shift()
        buffer.currentHeight -= result.len() as i32;
        // TODO: 背压放水策略： buffer.currentHeight < buffer.waterThrotth/2
        if buffer.currentHeight < buffer.waterThrotth {
            call_java_open_back_pressure(channelId) // 通知前端放水
        }
        return Ok(result);
    }
    let promise_out = buffer.waitter.lock().unwrap();
    // 创建新线程
    let thread_promise_out = promise_out.waker.clone();
    // if waitter.waker {
    //     Err(custom_error("op_rust_to_js_system_buffer", "超过"))
    // }
    promise_out.status = PromiseType::FULFILLED;
    if let Some(waker) = thread_promise_out.take() {
        waker.wake()
    }
    // thread::spawn(move || {
    //     // 通知执行器定时器已经完成，可以继续`poll`对应的`Future`了
    //     promise_out.status = PromiseType::FULFILLED;
    //     if let Some(waker) = thread_promise_out.take() {
    //         waker.wake()
    //     }
    // });
    return Ok(vec![0]);
}

/// deno-js 轮询访问这个方法，以达到把rust数据传递到deno-js的过程
#[op]
pub fn op_rust_to_js_buffer() -> Result<Vec<u8>, AnyError> {
    let box_data = BUFFER_RESOLVE.lock().pop();
    match box_data {
        Some(r) => Ok(r),
        None => Err(custom_error("op_rust_to_js_buffer", "未找到数据")),
    }
}

/// 负责消息通知
#[op]
pub fn op_rust_to_js_app_notification() -> Result<Vec<u8>, AnyError> {
    let box_data = BUFFER_NOTIFICATION.lock().pop();
    match box_data {
        Some(r) => Ok(r),
        None => Err(custom_error("op_rust_to_js_app_notification", "未找到数据")),
    }
}

/// 负责存储消息
#[op]
pub fn op_rust_to_js_set_app_notification(buffer: ZeroCopyBuf) {
    BUFFER_NOTIFICATION.lock().push(buffer.to_vec());
}
