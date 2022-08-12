#[cfg(target_os = "android")]
use crate::android::android_inter;
use crate::js_bridge::call_android_function;
#[cfg(target_os = "android")]
use android_logger::Config;
use deno_core::error::{custom_error, AnyError};
use deno_core::parking_lot::Mutex;
use deno_core::{op, ZeroCopyBuf};
use lazy_static::*;
use log::{debug, error, info, Level};
use std::result::Result::Ok;
use std::str;

// 添加一个全局变量来缓存信息，让js每次来拿，防止js内存爆炸
lazy_static! {
    // pub(crate) static ref BUFFER_HANDLER: Mutex<Vec<Vec<u8>>> = Mutex::new(vec![]);
  pub(crate) static ref BUFFER_RESOLVE: Mutex<Vec<Vec<u8>>> = Mutex::new(vec![]);
}

#[op]
pub fn op_js_to_rust_buffer(buffer: ZeroCopyBuf) {
    #[cfg(target_os = "android")]
    android_logger::init_once(
        Config::default()
            .with_min_level(Level::Debug)
            .with_tag("deno_runtime::rust_to_js_buffer"),
    );
    log::info!("i am op_js_to_rust_buffer {:?}", buffer);
    call_android_function::call_android(buffer.to_vec()); // 通知FFI函数
}

#[op]
pub fn op_eval_js(buffer: ZeroCopyBuf) {
    call_android_function::call_android_evaljs(buffer.to_vec()); // 通知FFI函数
}

// #[op]
// pub fn op_rust_to_js_hander() -> Result<Vec<u8>, AnyError> {
//     let box_data = BUFFER_HANDLER.lock().pop();
//     match box_data {
//         Some(r) => Ok(r),
//         None => Err(custom_error("op_rust_to_js_hander", "未找到数据")),
//     }
// }

#[op]
pub fn op_rust_to_js_buffer() -> Result<Vec<u8>, AnyError> {
    let box_data = BUFFER_RESOLVE.lock().pop();
    match box_data {
        Some(r) => Ok(r),
        None => Err(custom_error("op_rust_to_js_buffer", "未找到数据")),
    }
}

// #[op]
// pub fn op_rust_to_js_buffer(
//     scope: &mut deno_core::v8::HandleScope,
//     this_arg: deno_core::serde_v8::Value,
//     cb: deno_core::serde_v8::Value,
// ) -> Result<(), AnyError> {
//     let cb = cb.to_v8(scope)?;
//     if !cb.is_function() {
//         bail!("must provide a callback");
//     }
//     let cb = unsafe { deno_core::v8::Local::<deno_core::v8::Function>::cast(cb) };
//     let this_arg = this_arg.to_v8(scope)?;
//     cb.call(scope, this_arg, &[]);
//     Ok(())
// }
// fn op_close(state: State, rid: u32, _buf: &mut [ZeroCopyBuf]) -> Result<u32, Error> {
//     debug!("close rid={}", rid);
//     let resource_table = &mut state.borrow_mut().resource_table;
//     resource_table
//         .close(rid)
//         .map(|_| 0)
//         .ok_or_else(bad_resource)
// }

// #[no_mangle]
// pub unsafe extern "C" fn js_to_rust_buffer(ptr: *const u8, len: usize) {
//     let buf = std::slice::from_raw_parts(ptr, len).to_vec();
//     call_android_function::call_android(buf); // 通知FFI函数
// }

// #[no_mangle]
// pub unsafe extern "C" fn eval_js(ptr: *const u8, len: usize) {
//     let buf = std::slice::from_raw_parts(ptr, len).to_vec();
//     call_android_function::call_android_evaljs(buf); // 通知FFI函数
// }

// #[no_mangle]
// pub unsafe extern "C" fn rust_to_js_buffer(ptr: *const u8, len: usize) -> *const u8 {
//     let buf = std::slice::from_raw_parts(ptr, len).to_vec();
//     android_logger::init_once(
//         Config::default()
//             .with_min_level(Level::Debug)
//             .with_tag("deno_runtime::rust_to_js_buffer"),
//     );
//     log::info!("i am rust_to_js_buffer {:?}", buf);
//     ptr
// }

// static mut STORED_FUNCTION: Option<extern "C" fn(u8) -> u8> = None;

// #[no_mangle]
// pub extern "C" fn &mut &mut store_function(func: Option<extern "C" fn(u8) -> u8>) {
//     unsafe { STORED_FUNCTION = func };
//     if func.is_none() {
//         println!("STORED_FUNCTION_2 cleared");
//     }
// }
// #[no_mangle]
// pub extern "C" fn store_function2(
//     f: &dyn Fn(Box<[u8]>) -> Box<[u8]>,
// ) -> &dyn std::ops::Fn(Box<[u8]>) -> Box<[u8]> {
//     f
// }
