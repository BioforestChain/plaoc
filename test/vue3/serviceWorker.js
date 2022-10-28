/// <reference lib="webworker" />
const sw = self;
let channelId = "";
// 向native层申请channelId
async function registerChannel() {
    return await fetch(`/channel/registry`).then(res => res.json());
}
sw.addEventListener("install", (event) => {
    event.waitUntil(sw.skipWaiting());
});
sw.addEventListener("activate", (event) => {
    event.waitUntil(sw.clients.claim());
});
const FETCH_EVENT_MAP = new Map();
// remember event.respondWith must sync call🐰
sw.addEventListener("fetch", async (event) => {
    // 如果id为空需要申请id
    if (channelId === "") {
        channelId = await registerChannel();
    }
    const request = event.request;
    console.log(`HttpRequestBuilder blob: ${JSON.stringify(await event.request.blob())},arrayBuffer: ${JSON.stringify(await event.request.arrayBuffer())},text: ${JSON.stringify(await event.request.text())}`);
    console.log(`HttpRequestBuilder ${request.method},url: ${request.url},headers: ${request.headers},body: ${request.body}`);
    // Build chunks
    const chunks = new HttpRequestBuilder(request, request.method, request.url, request.headers, request.body);
    let responseStreamController = null;
    // 生成结构体
    const fetchTask = {
        event,
        response: new Response(),
        responseStream: new ReadableStream({
            start(controller) {
                responseStreamController = controller;
            }
        }),
        responseStreamController,
    };
    // 存起来
    FETCH_EVENT_MAP.set(String(channelId), fetchTask);
    // 迭代发送
    for await (const chunk of chunks) {
        do {
            const { success } = await fetch(`/channel/${channelId}/chunk=${chunk}`)
                .then(res => res.json(), _ => ({ success: false }));
            if (success) {
                break;
            }
            await sleep(10);
        } while (true);
    }
});
const encodeToHex = (reqId, data) => {
    return `${reqId.toString().padStart(4, '0')}:${hexEncode(data)}`;
};
class HttpRequestBuilder {
    constructor(request, method, url, header, body) {
        Object.defineProperty(this, "request", {
            enumerable: true,
            configurable: true,
            writable: true,
            value: request
        });
        Object.defineProperty(this, "method", {
            enumerable: true,
            configurable: true,
            writable: true,
            value: method
        });
        Object.defineProperty(this, "url", {
            enumerable: true,
            configurable: true,
            writable: true,
            value: url
        });
        Object.defineProperty(this, "header", {
            enumerable: true,
            configurable: true,
            writable: true,
            value: header
        });
        Object.defineProperty(this, "body", {
            enumerable: true,
            configurable: true,
            writable: true,
            value: body
        });
        Object.defineProperty(this, "reqId", {
            enumerable: true,
            configurable: true,
            writable: true,
            value: HttpRequestBuilder.getReqId()
        });
        Object.defineProperty(this, "bodyId", {
            enumerable: true,
            configurable: true,
            writable: true,
            value: HttpRequestBuilder.getBodyId(this.reqId)
        });
    }
    static getReqId() {
        const reqId = HttpRequestBuilder.REQ_ID[0];
        HttpRequestBuilder.REQ_ID[0] += 2; // 0, 2 ,4,6
        return reqId;
    }
    static getBodyId(reqId) {
        const bodyId = HttpRequestBuilder.BODY_ID[0];
        HttpRequestBuilder.BODY_ID[0] = reqId + 1; // 1,3,5,7
        return bodyId;
    }
    async *[Symbol.asyncIterator]() {
        const headerId = this.reqId; // 偶数为头
        const bodyId = this.bodyId; // 奇数为body
        console.log("headerId:", headerId, "bodyId:", bodyId);
        // 只传递body
        yield encodeToHex(headerId, `${this.url}|${JSON.stringify(this.header)}`);
        // 如果body为空
        if (!this.body) {
            return;
        }
        const reader = this.body.getReader();
        while (true) {
            const { done, value } = await reader.read();
            if (done) {
                break;
            }
            yield `${bodyId.toString().padStart(4, '0')}:${value}`;
        }
    }
}
Object.defineProperty(HttpRequestBuilder, "REQ_ID", {
    enumerable: true,
    configurable: true,
    writable: true,
    value: new Uint16Array(1)
});
Object.defineProperty(HttpRequestBuilder, "BODY_ID", {
    enumerable: true,
    configurable: true,
    writable: true,
    value: new Uint16Array(1)
});
// return data 🐯
sw.addEventListener('message', event => {
    console.log("8999", typeof event.data)
    if (typeof event.data !== 'string') return
    console.log("xxxxx,", event.data);
    const [channelId, reqId, dataHex] = String(event.data).split(':');
    const chunk = String(dataHex).split(",");
    console.log(`serviceWorkerdata1data=> ${JSON.stringify(chunk)},reqId:${reqId}`);
    const fetchTask = FETCH_EVENT_MAP.get(channelId);
    // 如果存在
    if (fetchTask) {
        console.log("responseStreamController:", fetchTask.responseStreamController);
        // body reqId为偶数
        if ((Number(reqId) % 2) === 0 && fetchTask.responseStreamController !== null) {
            fetchTask.responseStreamController.enqueue(chunk);
        }
        else {
            /**
            headers?: HeadersInit;
            status?: number;
            statusText?: string;
            */
            const data = hexDecode(chunk);
            console.log(`serviceWorkerdatadata=> ${JSON.stringify(data)}`);
            const [headers, status, statusText] = JSON.parse(data);
            fetchTask.event.respondWith(fetchTask.response = new Response(fetchTask.responseStream, { headers, status, statusText }));
        }
    }
});
function hexEncode(data) {
    const encoder = new TextEncoder();
    return encoder.encode(data);
}
function hexDecode(buffer) {
    return new TextDecoder().decode(new Uint8Array(buffer));
}
/**
 * 等待函数
 * @param delay
 * @returns
 */
const sleep = (delay) => new Promise((resolve) => setTimeout(resolve, delay));
