// #![cfg(target_os = "android")]
use crate::js_bridge::{
    call_js_function::{BUFFER_INSTANCES, BUFFER_SYSTEM},
};
#[cfg(target_os = "android")]
use crate::module_loader::AssetsModuleLoader;
use crate::my_deno_runtime::bootstrap_deno_fs_runtime;
#[cfg(target_os = "android")]
use crate::my_deno_runtime::bootstrap_deno_runtime;
use android_logger::Config;
use jni::{
    objects::{JObject, JString},
    JNIEnv,
};
use jni_sys::{jbyteArray};
use log::Level;
use bytes::Bytes;
#[cfg(target_os = "android")]
use ndk::asset::{Asset, AssetManager};
#[cfg(target_os = "android")]
use ndk_sys::AAssetManager;
use serde::Deserialize;
use std::{fmt};
#[cfg(target_os = "android")]
use std::ptr::NonNull;
#[cfg(target_os = "android")]
use std::sync::Arc;

/// 初始化的一些操作
#[no_mangle]
#[tokio::main]
pub async extern "system" fn Java_info_bagen_rust_plaoc_DenoService_onlyReadRuntime(
    env: JNIEnv,
    _context: JObject,
    _jasset_manager: JObject,
    terget: JString,
) {
    android_logger::init_once(
        Config::default()
            .with_min_level(Level::Debug)
            .with_tag("myrust::BFS"),
    );
    log::info!(" onlyReadRuntime :启动BFS后端 !!");
    #[allow(unused_unsafe)]
    let _asset_manager_ptr = unsafe {
        #[cfg(target_os = "android")]
        ndk_sys::AAssetManager_fromJava(env.get_native_interface(), _jasset_manager.cast())
    };
    let _entrance: String = env.get_string(terget).unwrap().into();
    #[cfg(target_os = "android")]
    bootstrap_deno_runtime(
        Arc::new(AssetsModuleLoader::from_ptr(
            NonNull::new(_asset_manager_ptr).unwrap(),
        )),
        &_entrance,
        // "/assets/hello_runtime.js",
    )
    .await
    .unwrap();
}

#[no_mangle]
#[tokio::main]
pub async extern "system" fn Java_info_bagen_rust_plaoc_DenoService_denoRuntime(
    env: JNIEnv,
    _context: JObject,
    path: JString,
) {
    android_logger::init_once(
        Config::default()
            .with_min_level(Level::Debug)
            .with_tag("myrust::BFS"),
    );
    let asset_path = String::from(env.get_string(path).unwrap());
    log::info!(" denoRuntime :启动BFS后端 !! => {}", asset_path);
    let runtime =  bootstrap_deno_fs_runtime(asset_path.as_str()).await;
    match runtime {
        Ok(_worker) => {
            log::info!("DenoService_denoRuntime end");
            // DenoRuntime::new(worker);
        }
        Err(e) => {
            log::info!("DenoService_denoRuntime error:{:?}", e);
        }
    }
}
/// 接收dwebview发送的chunk请求二进制数据 dwebview 的所有数据通过这个通道传输到deno-js
#[no_mangle]
#[tokio::main]
pub async extern "system" fn Java_info_bagen_rust_plaoc_DenoService_backDataToRust(
    env: JNIEnv,
    _context: JObject,
    byte_data: jbyteArray, //  /channel/354481793036294/chunk=
)  {
    let scanner_data = env.convert_byte_array(byte_data).unwrap();
    let data_string = std::str::from_utf8(&scanner_data).unwrap().to_string();
    log::info!(" backDataToRust:{:?}", &data_string);
    
    let mut buffer_data = BUFFER_INSTANCES.lock().unwrap();

    if buffer_data.full {
        log::info!(" backDataToRust 已经阻塞了:{:?}", &buffer_data.full);
        // return -1 as jint;
    }
    let is_full = buffer_data.push(Bytes::from(scanner_data)).unwrap();
    log::info!(" backDataToRust is_full:{:?},current_height:{:?}", &is_full,buffer_data.current_height);
    // buffer_data.push(scanner_data);
    // 已经存在等待者，直接将数据交给等待者
    // if buffer_data.has_waitter() {
    //     // transfrom 'a to 'ststic lifetime
    //     // 完成等待者
    //     buffer_data.resolve_waitter(scanner_data.clone());
    //     // return 0 as jint;
    //     // 已经阻塞
    //     if buffer_data.full {
    //         log::info!(" backDataToRust 已经阻塞了:{:?}", &buffer_data.full);
    //         // return -1 as jint;
    //     }
    //     let is_full = buffer_data.push(scanner_data).unwrap();
    //     log::info!(" backDataToRust is_full:{:?}", &is_full);
    // }

    // buffer_data::init_waitter();
    // log::info!(" backDataToRust is_full:{:?}", &is_full);
    // 1 代表已经被阻塞
    // return if is_full { 1 as jint } else { 0 as jint };
}

/// 接收返回的系统API二进制数据 deno-js请求kotlin(系统函数)返回值走这里
#[no_mangle]
#[tokio::main]
pub async extern "C" fn Java_info_bagen_rust_plaoc_DenoService_backSystemDataToRust(
    env: JNIEnv,
    _context: JObject,
    byte_data: jbyteArray,
) {
    let scanner_data = env.convert_byte_array(byte_data).unwrap();
    let data_string = std::str::from_utf8(&scanner_data).unwrap();
    log::info!(" backSystemDataToRust:{:?}", data_string);
    BUFFER_SYSTEM.lock().unwrap().push(scanner_data.to_vec());
    //  let token = get_channel_id(data_string.to_string()).unwrap();
    //  log::info!(" backDataToRust token:{:?}", &token);
    //  let mut buffer_map = BUFFER_INSTANCES_MAP.lock();
    //  let buffer_instance = BufferInstance::new();
    //  // 存入map
    //      buffer_map.insert(token.clone(), Mutex::new(buffer_instance));
    //  let mut buffer_data = buffer_map.get(&token).expect("not found this BufferInstance 🥲").lock();
    //  // 已经存在等待者，直接将数据交给等待者
    //  if buffer_data.has_waitter() {
    //      let data: &'static Vec<u8> = Box::leak(Box::new(scanner_data));
    //      buffer_data.resolve_waitter(data);
    //      return 0 as jint;
    //  }
    //  // 已经阻塞
    //  if buffer_data.full {
    //      return -1 as jint;
    //  }
    //  let is_full = buffer_data.push(scanner_data).unwrap();
    //  // 1 代表已经被阻塞
    //  return if is_full { 1 as jint } else { 0 as jint };
}

#[allow(dead_code)]
/// 截取channelId /channel/354481793036294/chunk=
fn get_channel_id(url: String) -> Result<String, &'static str> {
    if !url.starts_with("/channel/") {
        return Err("not found channel request!");
    }
    let channel_id = &url[url.find("/channel/").unwrap() + 9..url.find("/chunk").unwrap()];
    return Ok(channel_id.to_owned());
}

#[derive(Deserialize, Debug)]
struct JsBackData {
    function: Vec<String>,
    data: String,
    channel_id: String,
}

/// 实现一个toString的trait
impl fmt::Display for JsBackData {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        write!(
            f,
            "{{channelId:{},function:{:?},data:{}}}",
            self.channel_id, self.function, self.data
        )
    }
}
