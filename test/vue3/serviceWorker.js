/**
 * @param value
 * @returns
 * @inline
 */
const isPromiseLike = (value) => {
    return (value instanceof Object &&
        typeof value.then === "function");
};

class PromiseOut {
    constructor() {
        Object.defineProperty(this, "promise", {
            enumerable: true,
            configurable: true,
            writable: true,
            value: void 0
        });
        Object.defineProperty(this, "is_resolved", {
            enumerable: true,
            configurable: true,
            writable: true,
            value: false
        });
        Object.defineProperty(this, "is_rejected", {
            enumerable: true,
            configurable: true,
            writable: true,
            value: false
        });
        Object.defineProperty(this, "is_finished", {
            enumerable: true,
            configurable: true,
            writable: true,
            value: false
        });
        Object.defineProperty(this, "value", {
            enumerable: true,
            configurable: true,
            writable: true,
            value: void 0
        });
        Object.defineProperty(this, "reason", {
            enumerable: true,
            configurable: true,
            writable: true,
            value: void 0
        });
        Object.defineProperty(this, "resolve", {
            enumerable: true,
            configurable: true,
            writable: true,
            value: void 0
        });
        Object.defineProperty(this, "reject", {
            enumerable: true,
            configurable: true,
            writable: true,
            value: void 0
        });
        Object.defineProperty(this, "_innerFinally", {
            enumerable: true,
            configurable: true,
            writable: true,
            value: void 0
        });
        Object.defineProperty(this, "_innerFinallyArg", {
            enumerable: true,
            configurable: true,
            writable: true,
            value: void 0
        });
        Object.defineProperty(this, "_innerThen", {
            enumerable: true,
            configurable: true,
            writable: true,
            value: void 0
        });
        Object.defineProperty(this, "_innerCatch", {
            enumerable: true,
            configurable: true,
            writable: true,
            value: void 0
        });
        this.promise = new Promise((resolve, reject) => {
            this.resolve = (value) => {
                try {
                    if (isPromiseLike(value)) {
                        value.then(this.resolve, this.reject);
                    }
                    else {
                        this.is_resolved = true;
                        this.is_finished = true;
                        resolve((this.value = value));
                        this._runThen();
                        this._innerFinallyArg = Object.freeze({
                            status: "resolved",
                            result: this.value,
                        });
                        this._runFinally();
                    }
                }
                catch (err) {
                    this.reject(err);
                }
            };
            this.reject = (reason) => {
                this.is_rejected = true;
                this.is_finished = true;
                reject((this.reason = reason));
                this._runCatch();
                this._innerFinallyArg = Object.freeze({
                    status: "rejected",
                    reason: this.reason,
                });
                this._runFinally();
            };
        });
    }
    onSuccess(innerThen) {
        if (this.is_resolved) {
            this.__callInnerThen(innerThen);
        }
        else {
            (this._innerThen || (this._innerThen = [])).push(innerThen);
        }
    }
    onError(innerCatch) {
        if (this.is_rejected) {
            this.__callInnerCatch(innerCatch);
        }
        else {
            (this._innerCatch || (this._innerCatch = [])).push(innerCatch);
        }
    }
    onFinished(innerFinally) {
        if (this.is_finished) {
            this.__callInnerFinally(innerFinally);
        }
        else {
            (this._innerFinally || (this._innerFinally = [])).push(innerFinally);
        }
    }
    _runFinally() {
        if (this._innerFinally) {
            for (const innerFinally of this._innerFinally) {
                this.__callInnerFinally(innerFinally);
            }
            this._innerFinally = undefined;
        }
    }
    __callInnerFinally(innerFinally) {
        queueMicrotask(async () => {
            try {
                await innerFinally(this._innerFinallyArg);
            }
            catch (err) {
                console.error("Unhandled promise rejection when running onFinished", innerFinally, err);
            }
        });
    }
    _runThen() {
        if (this._innerThen) {
            for (const innerThen of this._innerThen) {
                this.__callInnerThen(innerThen);
            }
            this._innerThen = undefined;
        }
    }
    _runCatch() {
        if (this._innerCatch) {
            for (const innerCatch of this._innerCatch) {
                this.__callInnerCatch(innerCatch);
            }
            this._innerCatch = undefined;
        }
    }
    __callInnerThen(innerThen) {
        queueMicrotask(async () => {
            try {
                await innerThen(this.value);
            }
            catch (err) {
                console.error("Unhandled promise rejection when running onSuccess", innerThen, err);
            }
        });
    }
    __callInnerCatch(innerCatch) {
        queueMicrotask(async () => {
            try {
                await innerCatch(this.value);
            }
            catch (err) {
                console.error("Unhandled promise rejection when running onError", innerCatch, err);
            }
        });
    }
}

class EasyMap extends Map {
    // private _map: Map<F, V>;
    constructor(creater, entries, transformKey = (v) => v, _afterDelete) {
        super(entries);
        Object.defineProperty(this, "creater", {
            enumerable: true,
            configurable: true,
            writable: true,
            value: creater
        });
        Object.defineProperty(this, "transformKey", {
            enumerable: true,
            configurable: true,
            writable: true,
            value: transformKey
        });
        Object.defineProperty(this, "_afterDelete", {
            enumerable: true,
            configurable: true,
            writable: true,
            value: _afterDelete
        });
    }
    static from(args) {
        return new EasyMap(args.creater, args.entries, args.transformKey, args.afterDelete);
    }
    forceGet(key, creater = this.creater) {
        const k = this.transformKey(key);
        if (super.has(k)) {
            return super.get(k);
        }
        const res = creater(key, k);
        super.set(k, res);
        return res;
    }
    tryGet(key) {
        return this.get(this.transformKey(key));
    }
    trySet(key, val) {
        return this.set(this.transformKey(key), val);
    }
    tryDelete(key) {
        return this.delete(this.transformKey(key));
    }
    tryHas(key) {
        return this.has(this.transformKey(key));
    }
    delete(key) {
        const res = super.delete(key);
        if (res && this._afterDelete) {
            this._afterDelete(key);
        }
        return res;
    }
    get [Symbol.toStringTag]() {
        return "EasyMap";
    }
    static call(_this, creater, entries, transformKey, _afterDelete) {
        if (!(_this instanceof EasyMap)) {
            throw new TypeError("please use new keyword to create EasyMap instance.");
        }
        const protoMap = new EasyMap(creater, entries, transformKey, _afterDelete);
        const protoMap_PROTO = Object.getPrototypeOf(protoMap);
        const protoMap_PROTO_PROTO = Object.getPrototypeOf(protoMap_PROTO);
        const mapProps = Object.getOwnPropertyDescriptors(protoMap_PROTO_PROTO);
        for (const key in mapProps) {
            if (key !== "constructor") {
                const propDes = mapProps[key];
                if (typeof propDes.value === "function") {
                    propDes.value = propDes.value.bind(protoMap);
                }
                else {
                    if (typeof propDes.get === "function") {
                        propDes.get = propDes.get.bind(protoMap);
                    }
                    if (typeof propDes.set === "function") {
                        propDes.set = propDes.set.bind(protoMap);
                    }
                }
                Object.defineProperty(_this, key, propDes);
            }
        }
        const easymapProps = Object.getOwnPropertyDescriptors(protoMap_PROTO);
        for (const key in easymapProps) {
            if (key !== "constructor") {
                const propDes = easymapProps[key];
                if (typeof propDes.value === "function") {
                    propDes.value = propDes.value.bind(protoMap);
                }
                else {
                    if (typeof propDes.get === "function") {
                        propDes.get = propDes.get.bind(protoMap);
                    }
                    if (typeof propDes.set === "function") {
                        propDes.set = propDes.set.bind(protoMap);
                    }
                }
                Object.defineProperty(_this, key, propDes);
            }
        }
        const thisProps = Object.getOwnPropertyDescriptors(protoMap);
        for (const key in thisProps) {
            if (key !== "constructor")
                Object.defineProperty(_this, key, {
                    enumerable: true,
                    configurable: true,
                    get() {
                        return Reflect.get(protoMap, key);
                    },
                    set(v) {
                        Reflect.set(protoMap, key, v);
                    },
                });
        }
        return _this;
    }
}

class EasyWeakMap extends WeakMap {
    constructor(creater, entries, transformKey = (v) => v, _afterDelete) {
        super(entries);
        Object.defineProperty(this, "creater", {
            enumerable: true,
            configurable: true,
            writable: true,
            value: creater
        });
        Object.defineProperty(this, "transformKey", {
            enumerable: true,
            configurable: true,
            writable: true,
            value: transformKey
        });
        Object.defineProperty(this, "_afterDelete", {
            enumerable: true,
            configurable: true,
            writable: true,
            value: _afterDelete
        });
    }
    static from(args) {
        return new EasyWeakMap(args.creater, args.entries, args.transformKey, args.afterDelete);
    }
    forceGet(key, creater = this.creater) {
        const k = this.transformKey(key);
        if (this.has(k)) {
            return this.get(k);
        }
        const res = creater(key, k);
        this.set(k, res);
        return res;
    }
    tryGet(key) {
        return this.get(this.transformKey(key));
    }
    trySet(key, val) {
        return this.set(this.transformKey(key), val);
    }
    tryDelete(key) {
        return this.delete(this.transformKey(key));
    }
    tryHas(key) {
        return this.has(this.transformKey(key));
    }
    delete(key) {
        const res = super.delete(key);
        if (res && this._afterDelete) {
            this._afterDelete(key);
        }
        return res;
    }
    get [Symbol.toStringTag]() {
        return "EasyWeakMap";
    }
    static call(_this, creater, entries, transformKey, _afterDelete) {
        if (!(_this instanceof EasyWeakMap)) {
            throw new TypeError("please use new keyword to create EasyWeakMap instance.");
        }
        const protoMap = new EasyWeakMap(creater, entries, transformKey, _afterDelete);
        const protoMap_PROTO = Object.getPrototypeOf(protoMap);
        const protoMap_PROTO_PROTO = Object.getPrototypeOf(protoMap_PROTO);
        const mapProps = Object.getOwnPropertyDescriptors(protoMap_PROTO_PROTO);
        for (const key in mapProps) {
            if (key !== "constructor") {
                const propDes = mapProps[key];
                if (typeof propDes.value === "function") {
                    propDes.value = propDes.value.bind(protoMap);
                }
                else {
                    if (typeof propDes.get === "function") {
                        propDes.get = propDes.get.bind(protoMap);
                    }
                    if (typeof propDes.set === "function") {
                        propDes.set = propDes.set.bind(protoMap);
                    }
                }
                Object.defineProperty(_this, key, propDes);
            }
        }
        const easymapProps = Object.getOwnPropertyDescriptors(protoMap_PROTO);
        for (const key in easymapProps) {
            if (key !== "constructor") {
                const propDes = easymapProps[key];
                if (typeof propDes.value === "function") {
                    propDes.value = propDes.value.bind(protoMap);
                }
                else {
                    if (typeof propDes.get === "function") {
                        propDes.get = propDes.get.bind(protoMap);
                    }
                    if (typeof propDes.set === "function") {
                        propDes.set = propDes.set.bind(protoMap);
                    }
                }
                Object.defineProperty(_this, key, propDes);
            }
        }
        const thisProps = Object.getOwnPropertyDescriptors(protoMap);
        for (const key in thisProps) {
            if (key !== "constructor")
                Object.defineProperty(_this, key, {
                    enumerable: true,
                    configurable: true,
                    get() {
                        return Reflect.get(protoMap, key);
                    },
                    set(v) {
                        Reflect.set(protoMap, key, v);
                    },
                });
        }
        return _this;
    }
}

/// <reference lib="webworker" />
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
        /// 开始向外发送数据，切片发送
        console.log(`HttpRequestBuilder ${request.method},url: ${request.url},body:${request.body}`);
        event.respondWith((async () => {
            const client = await self.clients.get(event.clientId);
            if (client === undefined) {
                return fetch(event.request);
            }
            const channelId = await CLIENT_FETCH_CHANNEL_ID_WM.forceGet(client);
            const task = FETCH_EVENT_TASK_MAP.forceGet({ event, channelId });
            // Build chunks
            const chunks = new HttpRequestBuilder(task.reqHeadersId, task.reqBodyId, request);
            // 迭代发送
            for await (const chunk of chunks) {
                fetch(`/channel/${channelId}/chunk=${chunk}`)
                    .then(res => res.text(), _ => ({ success: false }));
            }
            return await task.po.promise;
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
            const headers = {};
            request.headers.forEach((value, key) => {
                if (key === "user-agent") { // user-agent 太长了先不要
                    return;
                }
                Object.assign(headers, { [key]: value });
            });
            // 传递headers
            yield contactToHex(uint16_to_binary(headersId), encoder.encode(JSON.stringify({ url: request.url, headers, method: request.method.toUpperCase() })), uint8_to_binary(0));
            console.log("有body数据传递1：", request.body);
            // 如果body为空
            if (request.body) {
                const reader = request.body.getReader();
                do {
                    const { done, value } = await reader.read();
                    if (done) {
                        break;
                    }
                    console.log("有body数据传递2：", value);
                    yield binaryToHex(contact(uint16_to_binary(bodyId), value, uint8_to_binary(0)));
                } while (true);
            }
            yield binaryToHex(contact(uint16_to_binary(bodyId), uint8_to_binary(1)));
        }
    }
    // return data 🐯
    self.addEventListener('message', event => {
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
        const fetchTask = FETCH_EVENT_TASK_MAP.get(`${channelId}-${headersId}`);
        // 如果存在
        if (fetchTask === undefined) {
            throw new Error("no found fetch task:" + returnId);
        }
        const responseContent = chunk.slice(0, -1);
        if (returnId === headersId) { // parse headers
            console.log("responseContent:", decoder.decode(responseContent));
            const { statusCode, headers } = JSON.parse(decoder.decode(responseContent));
            fetchTask.responseHeaders = headers;
            fetchTask.responseStatusCode = statusCode;
            fetchTask.po.resolve(new Response(fetchTask.responseBody.stream, {
                status: statusCode,
                headers,
            }));
        }
        else if (returnId === bodyId) { // parse body
            console.log("文件流推入", channelId, headersId, bodyId, responseContent.byteLength);
            fetchTask.responseBody.controller.enqueue(responseContent);
        }
        else {
            throw new Error("should not happen!! NAN? " + returnId);
        }
        if (end) {
            console.log("文件流关闭", channelId, headersId, bodyId);
            fetchTask.responseBody.controller.close();
        }
    });
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
