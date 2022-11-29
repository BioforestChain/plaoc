// #![cfg(target_os = "android")]
use crate::js_bridge::{call_android_function};
use android_logger::Config;
use std::sync::{Mutex, Arc};
use deno_core::error::{custom_error, AnyError};
use deno_core::{op, ZeroCopyBuf};
use lazy_static::*;
use log::{ Level};
use std::{str};
use super::promise::{BufferInstance, BufferTask};

// pub type Db = Arc<Mutex<HashMap<String,Bytes>>>;
pub type Db = Arc<Mutex<BufferTask>>;
// 添加一个全局变量来缓存信息，让js每次来拿，防止js内存爆炸,这里应该用channelId来定位每条消息，而不是用现在的队列
lazy_static! {
    // 消息通道
    pub(crate) static ref BUFFER_NOTIFICATION: Mutex<Vec<Vec<u8>>> = Mutex::new(vec![]);
    // 系统操作通道
    pub(crate) static ref BUFFER_SYSTEM: Mutex<Vec<Vec<u8>>> = Mutex::new(vec![]);

    pub(crate) static ref BUFFER_INSTANCES: Arc<Mutex<BufferInstance>> =  Arc::new(Mutex::new(BufferInstance::new()));
    pub(crate) static ref BUFFER_INSTANCES_MAP: Db = Arc::new(Mutex::new(BufferTask::new()));
    // pub(crate) static ref BUFFER_INSTANCES_MAP: Db = Arc::new(Mutex::new(BufferTask::new()));
}


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
pub fn op_rust_to_js_system_buffer(head_view:String) -> Result<Vec<u8>, AnyError> {
    let mut buffer_task = BUFFER_INSTANCES_MAP.lock().unwrap();
    let buffer = buffer_task.get(head_view);
    
    Ok(buffer)
}

/// chunk data 
/// serviceworker->kotlin(backDataToRust)->rust(Java_info_bagen_rust_plaoc_DenoService_backDataToRust)->BUFFER_INSTANCES
/// deno-js(loop function op_rust_to_js_buffer )
/// 
/// deno-js 轮询访问这个方法，以达到把rust数据传递到deno-js的过程 也就是说这里传递的是chunk数据
#[op]
pub fn op_rust_to_js_buffer() -> Result<Vec<u8>, AnyError> {

    let mut buffer =  BUFFER_INSTANCES.lock().unwrap();

    let result = buffer.shift();
    // log::info!(" op_rust_to_js_buffer 🤩 result:{:?}", &result);
    
    // 如果为空
    if result.is_empty() {
        return Ok(vec![0]);
    }
    log::info!(" op_rust_to_js_buffer 😻 current_height:{:?},water_threshold:{:?}", buffer.current_height,buffer.water_threshold);
    // TODO: 背压放水策略： buffer.current_height < buffer.water_throtth/2
    if buffer.full && buffer.current_height < buffer.water_threshold {
        buffer.full = false;
        call_android_function::call_java_open_back_pressure() // 通知前端放水
    }
    log::info!(" op_rust_to_js_buffer result:{:?}", &result);
    Ok(result.to_vec())
    // 初始化一个等待者
    // let waitter = buffer.init_waitter();
    // log::info!(" op_rust_to_js_buffer 👾 cache:{:?},waitter:{:?}", &result,&buffer.has_waitter());
    // let promise_out =  waitter.lock().unwrap();
    // let buffer = promise_out;
    // log::info!(" op_rust_to_js_buffer buffer 🤖:{:?}", &buffer);
    // match buffer {
    //     Some(data) => Ok(data),
    //     None => Ok(vec![0])
    // }
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

