// #![cfg(target_os = "android")]
use crate::android::android_inter;
use crate::js_bridge::call_android_function;
use android_logger::Config;
use deno_core::error::{custom_error, AnyError};
use deno_core::parking_lot::Mutex;
use deno_core::{op, ZeroCopyBuf};
use lazy_static::*;
use log::{debug, error, info, Level};
use std::result::Result::Ok;
use std::str;

// 添加一个全局变量来缓存信息，让js每次来拿，防止js内存爆炸,这里应该用channelId来定位每条消息，而不是用现在的队列
lazy_static! {
    // 消息通道
    pub(crate) static ref BUFFER_NOTIFICATION: Mutex<Vec<Vec<u8>>> = Mutex::new(vec![]);
    pub(crate) static ref BUFFER_RESOLVE: Mutex<Vec<Vec<u8>>> = Mutex::new(vec![]);
    // 系统操作通道
    pub(crate) static ref BUFFER_SYSTEM: Mutex<Vec<Vec<u8>>> = Mutex::new(vec![]);
    pub(crate) static ref BUFFER_SYSTEM_ARC: Mutex<Vec<Vec<u8>>> = Mutex::new(vec![]);
}

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
pub fn op_rust_to_js_system_buffer() -> Result<Vec<u8>, AnyError> {
    let box_data = BUFFER_SYSTEM.lock().pop();
    match box_data {
        Some(r) => Ok(r),
        None => Err(custom_error("op_rust_to_js_system_buffer", "未找到数据")),
    }
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

/**
/// call_js_functions
/// 这里的UUID是channnel的uuid
const BUFFER_INSTANCES_MAP = new Map<UUID( xxx-xxxx-xxxx-xxxx) ,{
    cache:[] as Array< Uint8Array >,
    waitter: undefined as PromiseOut<Uint8Array> |undefined,
    currentHeight: 0,
    waterThrotth: 1024 * 1024 * 8 // 8MB
}>();

op_rust_to_js_system_buffer = async (token)=>{
    const buffer = BUFFER_INSTANCES_MAP.lock().forceGet(token)

    if(buffer.cache.length > 0){
        const result = buffer.cache.shift();
        buffer.currentHeight -= result.length;
        // TODO: 背压放水策略： buffer.currentHeight < buffer.waterThrotth/2
        if(buffer.currentHeight < buffer.waterThrotth){
            Java_open_back_pressure(token)
        }
        return result;
    }
    if(buffer.waitter){
        throw new Error("xxxx");
    }
    const waitter = new PromiseOut();
    buffer.waitter = waitter;
    return await waitter.promise;
}

/// deno
Java_info_bagen_rust_plaoc_DenoService_backSystemDataToRust = (token:string, data:Uint8Array)=>{
    const buffer = BUFFER_INSTANCES_MAP.lock().get(token);
    if(buffer.waitter){
         buffer.waitter.resolve();
         buffer.waitter = undefined
         return 0 // 0 代表没有阻塞
    }

    if(buffer.currentHeight >= buffer.waterThrotth) {
        throw
    }
    buffer.cache.push(data)
    buffer.currentHeight += data.length;
    return buffer.currentHeight >= buffer.waterThrotth? 1: 0 // 1 代表已经被阻塞
}
Java_open_back_pressure = (token:string)=>{
    Channel.get(token).webview.evalJavascript(`nav.serviceWorker.postMessage({cmd::"openbackpressure"})`);
}


// service worker

let back_pressure?:PromiseOut<void>;

const url_queue = [];
let runnning = false
const queueFetch = async <R>(url:string)=>{
    const task = new PromiseOut<R>();
    url_queue.push({url,task});
    _runFetch();
    return task.promise
}
const _runFetch = async()=>{
  if(runnning){return}
    running = true;
    while(true){
        const item = url_queue.shift();
        if(item===undefined){
            break
        }
        if(back_pressure){
            await back_pressure.promise
        }
        if(await fetch(item.url).then(res=>res.text()) === 'ok'){
            back_pressure = new PromiseOut()
        }
    }
    running = false
}

on("fetch",event=> {
    for(const channel of channels){
        const matchResult = channel.match(event.request);
        if(matchResult){
            event.responseWith(channel.handle(matchResult))
        }
    }

    event.responseWith((async()=>{
        for await(const chunk of RequestBuidler.fromFetchEvent(event)){
            await queueFetch('duplex?chunk=${chunk}').then(res=>res?.text())
        }
    })())
});

on('message', event=>{
    if( matchBackPressureOpen(event.data)) {
        back_pressure?.resolve()
        return
    }

    if(mactchOpenChannel(event.data)){
        channels.push(event.data) // { type: "pattern", url:"" }
    }
})
/// web.js
while(true) {
    await requestAnimationFrame.promise();
    canvas.setData = new ImageData(await fetch('/api/blur',{method:"POST", body:canvas.toBlob()}).then(res=>res.arrayBuffer()));
}

// requestAnimationFream(()=>{
//     fetch('/api/blur',{method:"POST", body:canvas.toBlob()})
// });


/// server.js
(async()=>{
    for await(const request of dwebview.openRequest('/api/:api_method',{mode:"pattern"|"static"})){
        await processApi(request)
    }
})();
dwebview.open('./index.html');


/// # static mode
(async()=>{
    for await(const request of dwebview.openRequest('/image/:api_method', new StaticMode(dir:import.meta.resolve('./www')))){
        await processImage(request)
    }
})();

// cache by service-worker
class Channels{
    push(config) {
        if(config.mode === 'static') {
            const cache = await caches.open(config.cacheName);
            await cache.addAll(config.files);
            const handler = (event)=>{
               return caches.match(event.request)
            }
        }
    }
}
 */
const Z: i32 = 1;
