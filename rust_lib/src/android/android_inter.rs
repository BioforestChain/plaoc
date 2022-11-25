#![allow(non_snake_case)]
// #![cfg(target_os = "android")]
use lazy_static::*;
use log::{debug, error, info};
use jni::{
    objects::{GlobalRef, JObject, JValue},
    sys::{jint, JNI_ERR, JNI_VERSION_1_4},
    JNIEnv, JavaVM, NativeMethod,
};
use jni_sys::jbyteArray;
use std::{ffi::c_void, sync::Mutex};

// 添加一个全局变量来缓存回调对象
lazy_static! {
    // jvm
    static ref JVM_GLOBAL: Mutex<Option<JavaVM>> = Mutex::new(None);
    //callback
    static ref JNI_CALLBACK: Mutex<Option<GlobalRef>> = Mutex::new(None);
     static ref JNI_JS_CALLBACK: Mutex<Option<GlobalRef>> = Mutex::new(None);
     static ref JNI_RUST_CALLBACK: Mutex<Option<GlobalRef>> = Mutex::new(None);
}

// 校验的包名
#[allow(unused_macros)]
macro_rules! app_package {
    () => {
        "info.bagen.rust.plaoc"
    };
}

// 检验的签名 hash-code 获取方式可使用 info.bagen.rust.plaoc.denoService.Utils.getSignInfoHashCode 方式获取
#[allow(unused_macros)]
macro_rules! signature {
    () => {
        -779219788
    };
}

/// 使用宏简化声明 NativeMethod 对象
/// ## Examples
/// ```
/// let method:NativeMethod = jni_method!(native_method, "(Ljava/lang/String;)V");
/// ```
macro_rules! jni_method {
    ( $method:tt, $signature:expr ) => {{
        jni::NativeMethod {
            name: jni::strings::JNIString::from(stringify!($method)),
            sig: jni::strings::JNIString::from($signature),
            fn_ptr: $method as *mut c_void,
        }
    }};
}

/// 动态库被 java 加载时 会触发此函数, 在此动态注册本地方法
#[no_mangle]
#[allow(non_snake_case)]
unsafe fn JNI_OnLoad(jvm: JavaVM, _reserved: *mut c_void) -> jint {
    let class_name: &str = "info/bagen/rust/plaoc/DenoService";
    let jni_methods = [
        // # 添加注册一个可以传递java回调对象的本地方法
        jni_method!(
            nativeSetCallback,
            "(Linfo/bagen/rust/plaoc/DenoService$IHandleCallback;)V"
        ),
        jni_method!(
            denoSetCallback,
            "(Linfo/bagen/rust/plaoc/DenoService$IDenoCallback;)V"
        ),
        jni_method!(
            rustCallback,
            "(Linfo/bagen/rust/plaoc/DenoService$IRustCallback;)V"
        ),
    ];

    register_natives(&jvm, class_name, jni_methods.as_ref());

    // 在动态库被java加载时, 缓存一个jvm到全局变量, 调用回调时使用
    let mut ptr_jvm = JVM_GLOBAL.lock().unwrap();
    *ptr_jvm = Some(jvm);
    JNI_VERSION_1_4
}

/// 方法实现
#[no_mangle]
pub fn nativeSetCallback(env: JNIEnv, _obj: JObject, callback: JObject) {
    // 创建一个全局引用,
    let callback = env.new_global_ref(JObject::from(callback)).unwrap();

    // 添加到全局缓存
    let mut ptr_fn = JNI_CALLBACK.lock().unwrap();
    *ptr_fn = Some(callback);
}
/// 方法实现
#[no_mangle]
pub fn denoSetCallback(env: JNIEnv, _obj: JObject, callback: JObject) {
    // 创建一个全局引用,
    let callback = env.new_global_ref(JObject::from(callback)).unwrap();

    // 添加到全局缓存
    let mut ptr_fn = JNI_JS_CALLBACK.lock().unwrap();
    *ptr_fn = Some(callback);
}

/// 方法实现
#[no_mangle]
pub fn rustCallback(env: JNIEnv, _obj: JObject, callback: JObject) {
    // 创建一个全局引用,
    let callback = env.new_global_ref(JObject::from(callback)).unwrap();

    // 添加到全局缓存
    let mut ptr_fn = JNI_RUST_CALLBACK.lock().unwrap();
    *ptr_fn = Some(callback);
}


unsafe fn register_natives(jvm: &JavaVM, class_name: &str, methods: &[NativeMethod]) -> jint {
    let env: JNIEnv = jvm.get_env().unwrap();
    let jni_version = env.get_version().unwrap();
    let version: jint = jni_version.into();

    info!("JNI Version : {:#?} ", jni_version);

    let clazz = match env.find_class(class_name) {
        Ok(clazz) => clazz,
        Err(e) => {
            error!("java class not found : {:?}", e);
            return JNI_ERR;
        }
    };
    let result = env.register_native_methods(clazz, &methods);

    if result.is_ok() {
        info!("register_natives : succeed");
        version
    } else {
        error!("register_natives : failed ");
        JNI_ERR
    }
}

/// 把数据传输给kotlin  回调 Callback 对象的 { void handleCallback(byte: byteArray) } 函数 deno-js -> rust->kotlin
pub fn call_java_callback(fun_type: &'static [u8]) {
    log::info!("i am call_java_callback {:?}", fun_type);
    call_jvm(&JNI_CALLBACK, move |obj: JObject, env: &JNIEnv| {
        // let response: JString = env
        //     .new_string(fun_type)
        //     .expect("Couldn't create java string!");
        let response: jbyteArray = env
            .byte_array_from_slice(fun_type)
            .expect("Couldn't create java string!");
        match env.call_method(obj, "handleCallback", "([B)V", &[JValue::from(response)]) {
            Ok(jvalue) => {
                debug!("callback succeed: {:?}", jvalue);
            }
            Err(e) => {
                error!("callback failed : {:?}", e);
            }
        }
    });
}

/// 把数据传输给kotlin 不包含返回值 deno-js -> rust->kotlin
pub fn deno_evaljs_callback(fun_type: &'static [u8]) {
    log::info!("i am deno_evaljs_callback {:?}", fun_type);
    call_jvm(&JNI_JS_CALLBACK, move |obj: JObject, env: &JNIEnv| {
        let response: jbyteArray = env
            .byte_array_from_slice(fun_type)
            .expect("Couldn't create java string!");
        match env.call_method(obj, "denoCallback", "([B)V", &[JValue::from(response)]) {
            Ok(jvalue) => {
                debug!("callback succeed: {:?}", jvalue);
            }
            Err(e) => {
                error!("callback failed : {:?}", e);
            }
        }
    });
}

/// rust把数据传输给kotlin rust -> kotlin
pub fn deno_rust_callback(fun_type: &'static [u8]) {
    log::info!("i am deno_rust_callback {:?}", fun_type);
    call_jvm(&JNI_RUST_CALLBACK, move |obj: JObject, env: &JNIEnv| {
        let response: jbyteArray = env
            .byte_array_from_slice(fun_type)
            .expect("Couldn't create java string!");
        match env.call_method(obj, "rustCallback", "([B)V", &[JValue::from(response)]) {
            Ok(jvalue) => {
                debug!("callback succeed: {:?}", jvalue);
            }
            Err(e) => {
                error!("callback failed : {:?}", e);
            }
        }
    });
}


/// # 封装jvm调用
fn call_jvm<F>(callback: &Mutex<Option<GlobalRef>>, run: F)
where
    F: Fn(JObject, &JNIEnv) + Send + 'static,
{
    let ptr_jvm = JVM_GLOBAL.lock().unwrap();
    if (*ptr_jvm).is_none() {
        return;
    }
    let ptr_fn = callback.lock().unwrap();
    if (*ptr_fn).is_none() {
        return;
    }
    let jvm: &JavaVM = (*ptr_jvm).as_ref().unwrap();

    match jvm.attach_current_thread_permanently() {
        Ok(env) => {
            let obj = (*ptr_fn).as_ref().unwrap().as_obj();
            run(obj, &env);

            // 检查回调是否发生异常, 如果有异常发生,则打印并清空
            if let Ok(true) = env.exception_check() {
                let _ = env.exception_describe();
                let _ = env.exception_clear();
                // let _ = env.throw_new("java/lang/Exception", "JNI抛出的异常！");
            }
        }
        Err(e) => {
            debug!("jvm attach_current_thread failed: {:?}", e);
        }
    }
}

// fn rust_result_to_java_result<'e, T>(
//     env: &JNIEnv<'e>,
//     result: anyhow::Result<T>,
// ) -> JObject<'e>
// where
//     T: Display,
// {
//     let (is_ok, value) = match result {
//         Ok(v) => (true, format!("{}", v)),
//         Err(e) => (false, format!("{:?}", e)),
//     };
//     create_java_result(env, is_ok, value)
// }
// fn actually_do_stuff<'a>(
//     env: JNIEnv<'a>,
//     code: JString,
// ) -> anyhow::Result<String>
// {
//     let code = String::from(env.get_string(code)?);
//     let intermediate_value = some_rust_function(code)?;
//     other_rust_function(intermediate_value)
// }
// /// 可以返回我想要的任何类型的 Result，并将其转换为 Java 类的实例以供 Java 代码使用。
// #[no_mangle]
// pub extern "C" fn Java_com_startup_hip_RustCode_doStuff<'a>(
//     env: JNIEnv<'a>,
//     _class: JClass,
//     code: JString,
// ) -> JObject<'a>
// {
//     rust_result_to_java_result(actually_do_stuff(env, code))
// }
