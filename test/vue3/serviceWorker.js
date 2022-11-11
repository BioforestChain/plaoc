/// <reference lib="webworker" />
import { PromiseOut } from "./deps/deno.land/x/bnqkl_util@1.1.1/packages/extends-promise-out/PromiseOut.js";
import { EasyMap } from "./deps/deno.land/x/bnqkl_util@1.1.1/packages/extends-map/EasyMap.js";
import { EasyWeakMap } from "./deps/deno.land/x/bnqkl_util@1.1.1/packages/extends-map/EasyWeakMap.js";
((self) => {
    const CLIENT_FETCH_CHANNEL_ID_WM = EasyWeakMap.from({
        creater(_client) {
            return registerChannel();
        }
    });
    self.addEventListener("install", () => {
        // 跳过等待
        self.skipWaiting();
    });
    self.addEventListener("activate", () => {
        // 立刻控制整个页面
        self.clients.claim();
    });
    const event_id_acc = new Uint16Array(1);
    const EVENT_ID_WM = EasyWeakMap.from({
        // deno-lint-ignore no-unused-vars
        creater(event) {
            return event_id_acc[0] += 2;
        }
    });
    const FETCH_EVENT_TASK_MAP = EasyMap.from({
        transformKey(key) {
            return key.channelId + "-" + EVENT_ID_WM.forceGet(key.event);
        },
        creater(key) {
            let bodyStreamController;
            const bodyStream = new ReadableStream({
                start(controller) {
                    bodyStreamController = controller;
                }
            });
            const reqId = EVENT_ID_WM.forceGet(key.event);
            return {
                reqHeadersId: reqId,
                reqBodyId: reqId + 1,
                channelId: key.channelId,
                po: new PromiseOut(),
                responseHeaders: {},
                responseStatusCode: 200,
                responseBody: { stream: bodyStream, controller: bodyStreamController }
            };
        }
    });
    // remember event.respondWith must sync call🐰
    self.addEventListener("fetch", (event) => {
        const request = event.request.clone();
        const path = new URL(request.url).pathname;
        // 资源文件不处理
        if (path.lastIndexOf(".") !== -1) {
            return;
        }
        event.respondWith((async () => {
            const client = await self.clients.get(event.clientId);
            if (client === undefined) {
                return fetch(event.request);
            }
            const channelId = await CLIENT_FETCH_CHANNEL_ID_WM.forceGet(client);
            console.log("FETCH_EVENT_TASK_MAP:", channelId);
            const task = FETCH_EVENT_TASK_MAP.forceGet({ event, channelId });
            /// 开始向外发送数据，切片发送
            console.log(`HttpRequestBuilder ${request.method},url: ${request.url}`);
            const headers = {};
            request.headers.forEach((value, key) => {
                if (key === "user-agent") { // user-agent 太长了先不要
                    return;
                }
                Object.assign(headers, { [key]: value });
            });
            // Build chunks
            const chunks = new HttpRequestBuilder(task.reqHeadersId, task.reqBodyId, request);
            // 迭代发送
            for await (const chunk of chunks) {
                await fetch(`/channel/${channelId}/chunk=${chunk}`)
                    .then(res => res.text(), _ => ({ success: false }));
            }
            return task.po.promise;
        })());
    });
    class HttpRequestBuilder {
        constructor(headersId, bodyId, request) {
            Object.defineProperty(this, "headersId", {
                enumerable: true,
                configurable: true,
                writable: true,
                value: headersId
            });
            Object.defineProperty(this, "bodyId", {
                enumerable: true,
                configurable: true,
                writable: true,
                value: bodyId
            });
            Object.defineProperty(this, "request", {
                enumerable: true,
                configurable: true,
                writable: true,
                value: request
            });
        }
        async *[Symbol.asyncIterator]() {
            const { request, headersId, bodyId } = this;
            console.log("headerId:", headersId, "bodyId:", bodyId);
            // 传递headers
            yield contactToHex(uint16_to_binary(headersId), encoder.encode(JSON.stringify({ url: request.url, headers: request.headers, method: request.method.toUpperCase() })), uint8_to_binary(0));
            // 如果body为空
            if (request.body) {
                const reader = request.body.getReader();
                do {
                    const { done, value } = await reader.read();
                    if (done) {
                        break;
                    }
                    yield binaryToHex(contact(uint16_to_binary(bodyId), value, uint8_to_binary(0)));
                } while (true);
            }
            yield binaryToHex(contact(uint16_to_binary(bodyId), uint8_to_binary(1)));
        }
    }
    // return data 🐯
    self.addEventListener('message', event => {
        console.log('addEventListenermessage', event.data);
        if (typeof event.data !== 'string')
            return;
        const data = JSON.parse(event.data);
        const returnId = data.returnId;
        const channelId = data.channelId;
        const chunk = hexToBinary(data.chunk);
        const end = chunk.subarray(-1)[0] === 1;
        const bodyId = returnId | 1;
        const headersId = bodyId - 1;
        console.log(`serviceWorker chunk=> ${chunk},end:${end}`);
        console.log("FETCH_EVENT_TASK_MAP message:", headersId, channelId);
        const fetchTask = FETCH_EVENT_TASK_MAP.get(`${channelId}-${headersId}`);
        // 如果存在
        if (fetchTask === undefined) {
            throw new Error("no found fetch task:" + returnId);
            // console.log("如果存在:", end, decoder.decode(chunk))
            // // body reqId为偶数
            // console.log(`填入数据=> ${chunk}`);
            // if (end == "false") {
            //   fetchTask.responseStreamController.enqueue(chunk);
            // } else {
            //   console.log(`请求结束返回数据`);
            //   fetchTask.responseStreamController.close();
            //   const data = hexDecode(chunk);
            //   console.log(`请求结束返回数据=> ${data}`);
            //   const [headers, status, statusText] = data.split("|");
            //   console.log("解析headers", headers, status, statusText)
            //   fetchTask.response = new Response(fetchTask.responseStream, { headers: {}, status: Number(status), statusText })
            //   fetchTask.done = false
            // }
            // fetchTask.event.respondWith(new Response(fetchTask.responseStream, { headers: {}, status: Number(status), statusText }))
        }
        const responseContent = chunk.slice(0, -1);
        if (returnId === headersId) {
            const { statusCode, headers } = JSON.parse(decoder.decode(responseContent));
            fetchTask.responseHeaders = headers;
            fetchTask.responseStatusCode = statusCode;
            fetchTask.po.resolve(new Response(fetchTask.responseBody.stream, {
                status: statusCode,
                headers,
            }));
        }
        else if (returnId === bodyId) {
            fetchTask.responseBody.controller.enqueue(responseContent.buffer);
        }
        else {
            throw new Error("should not happen!! NAN? " + returnId);
        }
        if (end) {
            fetchTask.responseBody.controller.close();
        }
    });
    /**
     *  创建ReadableStream
     * @param arrayBuffer
     * @param chunkSize 64 kib
     * @returns
     */
    // deno-lint-ignore no-unused-vars
    function createReadableStream(arrayBuffer, chunkSize = 64 * 1024) {
        if (arrayBuffer.byteLength === 0)
            return null;
        return new ReadableStream({
            start(controller) {
                const bytes = new Uint8Array(arrayBuffer);
                for (let readIndex = 0; readIndex < bytes.byteLength;) {
                    controller.enqueue(bytes.subarray(readIndex, readIndex += chunkSize));
                }
                controller.close();
            }
        });
    }
    const contact = (...arrs) => {
        const length = arrs.reduce((l, a) => l += a.length, 0);
        const r = new Uint8Array(length);
        let walk = 0;
        for (const arr of arrs) {
            r.set(arr, walk);
            walk += arr.length;
        }
        return r;
    };
    const contactToHex = (...arrs) => {
        const hexs = [];
        for (const arr of arrs) {
            hexs.push(binaryToHex(arr));
        }
        return hexs.join(",");
    };
    const uint16_to_binary = (num) => {
        const r = new Uint16Array([num]);
        return new Uint8Array(r.buffer);
    };
    const uint8_to_binary = (num) => {
        return new Uint8Array([num]);
    };
    const encoder = new TextEncoder();
    const decoder = new TextDecoder();
    const binaryToHex = (binary) => {
        // let hex = '';
        // for (const byte of binary) {
        //   hex+= byte.toString()
        // }
        return binary.join();
    };
    const hexToBinary = (hex) => {
        return new Uint8Array(hex.split(",").map(v => +v));
    };
    // function hexEncode(data: string) {
    //   return encoder.encode(data);
    // }
    // function hexDecode(buffer: ArrayBuffer) {
    //   return new TextDecoder().decode(new Uint8Array(buffer));
    // }
    // 向native层申请channelId
    async function registerChannel() {
        return await fetch(`/channel/registry`).then(res => res.text());
    }
})(self);
