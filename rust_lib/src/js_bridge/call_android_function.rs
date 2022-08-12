#[cfg(target_os = "android")]
use crate::android::android_inter;
use crate::web_socket::ws::TopicsRequest;
use std::sync::mpsc::{Receiver, Sender};
use std::{
    collections::HashMap,
    sync::{mpsc, Arc},
};

/// 调用android方法
pub fn call_android(bit: Vec<u8>) {
    // let fun_type = &handle.function;
    // let public_key = &handle.public_key;
    // let data = &handle.data;
    // 转换为static str
    let callback = Box::leak(bit.into_boxed_slice());
    #[cfg(target_os = "android")]
    android_inter::call_java_callback(callback);
}

/// 调用android方法执行evenjs
pub fn call_android_evaljs(bit: Vec<u8>) {
    // let fun_type = &handle.function;
    // let public_key = &handle.public_key;
    // let data = &handle.data;
    // 转换为static str
    let callback = Box::leak(bit.into_boxed_slice());
    #[cfg(target_os = "android")]
    android_inter::deno_evaljs_callback(callback);
}

// #[allow(dead_code)]
// pub struct HandleFunction {
//     fun_type: Vec<String>,
//     pub sender: Arc<Sender<&'static str>>,
//     pub receiver: Arc<Receiver<&'static str>>,
//     fun_map: HashMap<String, String>,
// }

// impl HandleFunction {
//     pub fn new() -> HandleFunction {
//         let operation_fun = vec![String::from("openDWebView"), String::from("openScanner")];
//         let mut my_map: HashMap<String, String> = HashMap::new();
//         for opera in operation_fun.iter() {
//             my_map.insert(opera.to_string(), opera.to_string());
//         }
//         let (tx, rx) = mpsc::channel();
//         // let fun_sender = Box::leak(Box::new(tx));
//         // CHANNEL_SENDER = fun_sender;

//         HandleFunction {
//             fun_map: my_map,
//             sender: Arc::new(tx),
//             receiver: Arc::new(rx),
//             fun_type: operation_fun,
//         }
//     }

//     /// 验证是否是允许操作的函数
//     pub fn handle_match(fun: &str) -> Result<&str, &str> {
//         match fun {
//             "openDWebView" => Ok(fun),
//             "openScanner" => Ok(fun),
//             _ => return Err("handle function Not fund"),
//         }
//     }
// }
