// #![cfg(target_os = "android")]
use crate::js_bridge::{call_android_function, executor};
use android_logger::Config;
use std::sync::{Mutex};
use deno_core::error::{custom_error, AnyError};
use deno_core::{op, ZeroCopyBuf};
use lazy_static::*;
use log::{ Level};
use std::{collections::HashMap, str};

use super::promise::{BufferInstance};

// 添加一个全局变量来缓存信息，让js每次来拿，防止js内存爆炸,这里应该用channelId来定位每条消息，而不是用现在的队列
lazy_static! {
    // 消息通道
    pub(crate) static ref BUFFER_NOTIFICATION: Mutex<Vec<Vec<u8>>> = Mutex::new(vec![]);
    pub(crate) static ref BUFFER_RESOLVE: Mutex<Vec<Vec<u8>>> = Mutex::new(vec![]);
    // 系统操作通道
    pub(crate) static ref BUFFER_SYSTEM: Mutex<Vec<Vec<u8>>> = Mutex::new(vec![]);

    pub(crate) static ref BUFFER_INSTANCES: Mutex<BufferInstance> =  Mutex::new(BufferInstance::new(vec![]));
    pub(crate) static ref BUFFER_INSTANCES_MAP: Mutex<HashMap<ChannelId, Mutex<BufferInstance > >> = Mutex::new(HashMap::new());
}

type ChannelId = String;

/// deno-js system data
/// send channel: deno-js(ops.op_js_to_rust_buffer)->rust(op_js_to_rust_buffer) -> kotlin(call_java_callback)
/// back channel: kotlin(backSystemDataToRust)->rust(Java_info_bagen_rust_plaoc_DenoService_backSystemDataToRust)->BUFFER_SYSTEM
/// deno-js(loop function op_rust_to_js_system_buffer get data for BUFFER_SYSTEM)
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
pub async fn op_rust_to_js_system_buffer(_channel_id:String) -> Result<Vec<u8>, AnyError> {
    // let map = BUFFER_INSTANCES_MAP.lock();
    // let mut  buffer = map.get(&channel_id).unwrap().lock();

    // if !buffer.cache.is_empty() {
    //     let result = buffer.shift();
        
    //     // TODO: 背压放水策略： buffer.current_height < buffer.water_throtth/2
    //     if buffer.full && buffer.current_height < buffer.water_threshold {
    //         buffer.full = false;
    //         call_android_function::call_java_open_back_pressure() // 通知前端放水 channel_id
    //     }
    //     return Ok(result);
    // }

    // let waitter = buffer.init_waitter().clone();
    // let buffer = waitter.await.as_ref().unwrap().clone();
    // return Ok(buffer.clone());
    let box_data = BUFFER_SYSTEM.lock().unwrap().pop();
    match box_data {
        Some(r) => Ok(r),
        None => Err(custom_error("op_rust_to_js_buffer", "未找到数据")),
    }
}

/// chunk data 
/// serviceworker->kotlin(backDataToRust)->rust(Java_info_bagen_rust_plaoc_DenoService_backDataToRust)->BUFFER_INSTANCES
/// deno-js(loop function op_rust_to_js_buffer )
/// 
/// deno-js 轮询访问这个方法，以达到把rust数据传递到deno-js的过程 也就是说这里传递的是chunk数据
#[op]
pub async fn op_rust_to_js_buffer() -> Result<Vec<u8>, AnyError> {
    log::info!(" op_rust_to_js_buffer 🥸  ");
    let mut buffer =  BUFFER_INSTANCES.lock().unwrap();
    let result = buffer.shift();
    log::info!(" op_rust_to_js_buffer 🤩 result:{:?}", &result);
    if !result.is_empty() {
        log::info!(" op_rust_to_js_buffer 😻 buffer.shift:{:?}", &result);
        // TODO: 背压放水策略： buffer.current_height < buffer.water_throtth/2
        if buffer.full && buffer.current_height < buffer.water_threshold {
            buffer.full = false;
            call_android_function::call_java_open_back_pressure() // 通知前端放水
        }
        log::info!(" op_rust_to_js_buffer result:{:?}", &result);
        return Ok(result);
    }
    // 初始化一个等待者
    let waitter = buffer.init_waitter();
    log::info!(" op_rust_to_js_buffer 👾 cache:{:?},waitter:{:?}", &result,&buffer.has_waitter());
    let buffer =  waitter.lock().unwrap().result.clone();
    log::info!(" op_rust_to_js_buffer buffer 🤖:{:?}", &buffer);
    match buffer {
        Some(data) => Ok(data),
        None => Ok(vec![0])
    }
    // return Ok(vec![0]);
}

/// 负责消息通知
#[op]
pub fn op_rust_to_js_app_notification() -> Result<Vec<u8>, AnyError> {
    let box_data = BUFFER_NOTIFICATION.lock().unwrap().pop();
    match box_data {
        Some(r) => Ok(r),
        None => Err(custom_error("op_rust_to_js_app_notification", "未找到数据")),
    }
}

/// 负责存储消息
#[op]
pub fn op_rust_to_js_set_app_notification(buffer: ZeroCopyBuf) {
    BUFFER_NOTIFICATION.lock().unwrap().push(buffer.to_vec());
}

