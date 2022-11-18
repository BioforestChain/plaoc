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
