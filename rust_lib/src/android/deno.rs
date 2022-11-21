// #![cfg(target_os = "android")]
use crate::js_bridge::call_js_function::{BUFFER_INSTANCES_MAP, BUFFER_SYSTEM};
#[cfg(target_os = "android")]
use crate::module_loader::AssetsModuleLoader;
use crate::my_deno_runtime::{bootstrap_deno_fs_runtime, bootstrap_deno_runtime};
use android_logger::Config;
use jni::{
    objects::{JObject, JString, JValue},
    JNIEnv,
};
use jni_sys::jbyteArray;
use log::Level;
#[cfg(target_os = "android")]
use ndk::asset::{Asset, AssetManager};
#[cfg(target_os = "android")]
use ndk_sys::AAssetManager;
use serde::Deserialize;
use std::fmt;
use std::ptr::NonNull;
use std::sync::Arc;

/// 初始化的一些操作

#[no_mangle]
#[tokio::main]
pub async extern "system" fn Java_info_bagen_rust_plaoc_DenoService_onlyReadRuntime(
    env: JNIEnv,
    _context: JObject,
    jasset_manager: JObject,
    terget: JString,
) {
    android_logger::init_once(
        Config::default()
            .with_min_level(Level::Debug)
            .with_tag("myrust::BFS"),
    );
    log::info!(" onlyReadRuntime :启动BFS后端 !!");
    let asset_manager_ptr = unsafe {
        #[cfg(target_os = "android")]
        ndk_sys::AAssetManager_fromJava(env.get_native_interface(), jasset_manager.cast())
    };
    let entrance: String = env.get_string(terget).unwrap().into();
    #[cfg(target_os = "android")]
    bootstrap_deno_runtime(
        Arc::new(AssetsModuleLoader::from_ptr(
            NonNull::new(asset_manager_ptr).unwrap(),
        )),
        &entrance,
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
    bootstrap_deno_fs_runtime(asset_path.as_str())
        .await
        .unwrap();
}
/// 接收返回的二进制数据 dwebview 的所有数据通过这个通道传输到deno-js
#[no_mangle]
#[tokio::main]
pub async extern "system" fn Java_info_bagen_rust_plaoc_DenoService_backDataToRust(
    env: JNIEnv,
    _context: JObject,
    token: JString,
    byteData: jbyteArray,
) {
    let scanner_data = env.convert_byte_array(byteData).unwrap();
    let data_string = std::str::from_utf8(&scanner_data).unwrap();
    // JString tramsfrom String
    let token_string: String = env.get_string(token).unwrap().into();
    log::info!(" backDataToRust:{:?}", data_string);
    let buffer =  BUFFER_INSTANCES_MAP.lock().get(&token_string).unwrap();

    if buffer.currentHeight >= buffer.waterThrotth {
        println!()
    }
    buffer.cache.push(scanner_data.to_vec());
    buffer.currentHeight += scanner_data.len() as i32;
    // 1 代表已经被阻塞
    if buffer.currentHeight >= buffer.waterThrotth {}
}

/// 接收返回的系统API二进制数据 deno-js请求kotlin(系统函数)返回值走这里
#[no_mangle]
#[tokio::main]
pub async extern "C" fn Java_info_bagen_rust_plaoc_DenoService_backSystemDataToRust(
    env: JNIEnv,
    _context: JObject,
    byteData: jbyteArray,
) {
    let scanner_data = env.convert_byte_array(byteData).unwrap();
    let data_string = std::str::from_utf8(&scanner_data).unwrap();
    log::info!(" backSystemDataToRust:{:?}", data_string);
    BUFFER_SYSTEM.lock().push(scanner_data.to_vec());
}

#[derive(Deserialize, Debug)]
struct JsBackData {
    function: Vec<String>,
    data: String,
    channelId: String,
}

/// 实现一个toString的trait
impl fmt::Display for JsBackData {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        write!(
            f,
            "{{channelId:{},function:{:?},data:{}}}",
            self.channelId, self.function, self.data
        )
    }
}
