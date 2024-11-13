use serde_json::json;

use crate::android::android_inter;

/// call_java_callback 有返回记录
/// deno_evaljs_callback 不需要返回记录

/// 调用android方法
pub fn call_android(bit: Vec<u8>) {
    // 转换为static str
    let buffer = Box::leak(bit.into_boxed_slice());
    android_inter::call_java_callback(buffer);
}

/// 调用android方法执行evenjs
pub fn call_send_zero_copy_buffer(bit: Vec<u8>) {
    // 转换为static str
    let buffer = Box::leak(bit.into_boxed_slice());
    // log::info!("rust#op_send_zero_copy_buffer3 --> {:?}", buffer.len());
    android_inter::deno_zerocopybuffer_callback(buffer);
}

/// 通知kotlin叫serviceWorker打开背压
pub fn call_java_open_back_pressure() {
    let data = json!(
    {
        "function":"openBackPressure",
        "data":""
    }
    );
    call_deno_rust(data.as_str().unwrap())
}

/// 通知kotlin 已经溢出了不要再发数据了
pub fn call_native_request_overflow() {
    let data = json!(
    {
        "function":"requestOverflow",
        "data":""
    }
    );
    call_deno_rust(data.as_str().unwrap())
}

// 把数据转化为[u8]并call kotlin
pub fn call_deno_rust(data: &str) {
    let bit = data.to_owned().as_bytes().to_vec();
    // 转换为static str
    let callback = Box::leak(bit.into_boxed_slice());
    android_inter::deno_rust_callback(callback);
}
