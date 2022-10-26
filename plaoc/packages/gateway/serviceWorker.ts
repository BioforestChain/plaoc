/// <reference lib="webworker" />

const sw = self as unknown as ServiceWorkerGlobalScope;
let channelId = "";
// 向native层申请channelId
async function registerChnnel() {
  return await fetch(`/channel/registry`).then(res => res.json());
}

sw.addEventListener("install", (event) => {
  event.waitUntil(sw.skipWaiting());
});

sw.addEventListener("activate", (event) => {
  event.waitUntil(sw.clients.claim());
});

const FETCH_EVENT_MAP = new Map<number,
  {
    event: FetchEvent, response?: Response, responseStream: ReadableStream,
    responseStreamController: ReadableStreamController<ArrayBuffer> | null
  }>()

// remember event.respondWith must sync call🐰
sw.addEventListener("fetch", async (event) => {
  // 如果id为空需要申请id
  if (channelId === "") {
    channelId = await registerChnnel()
  }
  const request = event.request;
  // Build chunks
  const chunks = new HttpRequestBuilder(request, request.method, request.url, request.headers, request.body);

  let responseStreamController: ReadableStreamController<ArrayBuffer> | null = null;
  // 生成结构体
  const fetchTask = {
    event,
    response: new Response(),
    responseStream: new ReadableStream({
      start(controller) {
        responseStreamController = controller
      }
    }),
    responseStreamController,
  }
  // 存起来
  FETCH_EVENT_MAP.set(chunks.reqId, fetchTask);
  // 迭代发送
  for await (const chunk of chunks) {
    do {
      const { success } = await fetch(`/channel/${channelId}/chunk=${chunk}`)
        .then(res => res.json(), _ => ({ success: false }));
      if (success) {
        break;
      }
      await sleep(10);
    } while (true)
  }

});

const encodeToHex = (reqId: number, data: string) => {
  return `${reqId.toString().padStart(4, '0')}${hexEncode(data)}`
}


export class HttpRequestBuilder {
  static REQ_ID = new Uint16Array(1);
  static getReqId() {
    const reqId = HttpRequestBuilder.REQ_ID[0]
    HttpRequestBuilder.REQ_ID[0] += 1;
    return reqId;
  }
  readonly reqId = HttpRequestBuilder.getReqId()

  constructor(
    readonly request: Request,
    readonly method: string,
    readonly url: string,
    readonly header: Headers,
    readonly body: ReadableStream<Uint8Array> | null
  ) { }


  async *[Symbol.asyncIterator]() {
    const headerId = this.reqId;// 偶数为头
    const bodyId = headerId.toString().padStart(4, '0');// 奇数为body

    yield encodeToHex(headerId, JSON.stringify([this.method, this.url, this.header]));
    // 如果body为空
    if (!this.body) {
      return
    }
    const reader = this.body.getReader()
    while (true) {
      const { done, value } = await reader.read()
      if (done) {
        break
      }

      yield `${bodyId},${value}`
    }
  }
}

// return data 🐯
sw.addEventListener('data', event => {
  console.log("serviceWorker data =>", event)
  // const [channelId, dataHex] = event.data.split(':');
  // const reqId = parseInt(dataHex.slice(0, 4), 16);
  // const chunk = hexDecode(dataHex.slice(4))
  // const fetchTask = FETCH_EVENT_MAP.get(channelId);
  // if (reqId | 1) {
  //   // body
  //   fetchTask.responseStreamController.enqueue(chunk)
  // } else {
  //   /**
  //   headers?: HeadersInit;
  //   status?: number;
  //   statusText?: string;
  //   */
  //   const [headers, status, statusText] = JSON.parse(chunk)

  //   fetchTask.event.responseWith(fetchTask.response = new Response(fetchTask.responseStream, { headers, status, statusText }))
  // }

})


function hexEncode(data: string) {
  const encoder = new TextEncoder();
  return encoder.encode(data);
}

function hexDecode(buffer: Uint8Array) {
  return new TextDecoder().decode(new Uint8Array(buffer));
}

/**
 * 等待函数
 * @param delay
 * @returns
 */
const sleep = (delay: number) =>
  new Promise((resolve) => setTimeout(resolve, delay));


