#![cfg(target_os = "android")]
use android_logger::Config;
use log::Level;
use ndk_sys::AAssetManager;
use serde::Deserialize;
use serde_json::from_str;
use std::{borrow::Borrow, fmt, sync::RwLock};
// 引用 jni 库的一些内容，就是上面添加的 jni 依赖
use crate::js_bridge::call_js_function;
use crate::js_bridge::call_js_function::BUFFER_RESOLVE;
use crate::module_loader::AssetsModuleLoader;
use crate::my_deno_runtime::{bootstrap_deno_runtime,bootstrap_deno_fs_runtime};
use jni::{
    objects::{JObject, JString, JValue},
    JNIEnv,
};

use jni_sys::jbyteArray;
use ndk::asset::{Asset, AssetManager};
use std::rc::Rc;
use std::sync::{Arc, Mutex};
use std::{collections::HashMap, ptr::NonNull};

/// 初始化的一些操作

#[no_mangle]
#[tokio::main]
pub async extern "system" fn Java_org_bfchain_rust_plaoc_DenoService_onlyReadRuntime(
    env: JNIEnv,
    _context: JObject,
    jasset_manager: JObject,
    terget:JString
) {
    android_logger::init_once(
        Config::default()
            .with_min_level(Level::Debug)
            .with_tag("myrust::BFS"),
    );
    log::info!(" onlyReadRuntime :启动BFS后端 !!");
    let asset_manager_ptr = unsafe {
        ndk_sys::AAssetManager_fromJava(env.get_native_interface(), jasset_manager.cast())
    };
    let entrance: String = env.get_string(terget).unwrap().into();
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
pub async extern "system" fn Java_org_bfchain_rust_plaoc_DenoService_denoRuntime(
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
    log::info!(" denoRuntime :启动BFS后端 !! => {}",asset_path);
    bootstrap_deno_fs_runtime( asset_path.as_str())
        .await
        .unwrap();
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

/// 接收返回的二进制数据
#[no_mangle]
#[tokio::main]
pub async extern "system" fn Java_org_bfchain_rust_plaoc_DenoService_backDataToRust(
    env: JNIEnv,
    _context: JObject,
    byteData: jbyteArray,
) {
    let scanner_data = env.convert_byte_array(byteData).unwrap();
    let data_string = std::str::from_utf8(&scanner_data).unwrap();
    log::info!(" backDataToRust:{:?}", data_string);
    BUFFER_RESOLVE.lock().push(scanner_data.to_vec());
}

/// 接收返回的系统API二进制数据
#[no_mangle]
#[tokio::main]
pub async extern "system" fn Java_org_bfchain_rust_plaoc_DenoService_backSystemDataToRust(
    env: JNIEnv,
    _context: JObject,
    byteData: jbyteArray,
) {
    let scanner_data = env.convert_byte_array(byteData).unwrap();
    let data_string = std::str::from_utf8(&scanner_data).unwrap();
    log::info!(" backSystemDataToRust:{:?}", data_string);
    BUFFER_SYSTEM.lock().push(scanner_data.to_vec());
}
