/// <reference lib="webworker" />

const sw = self as unknown as ServiceWorkerGlobalScope;
let channelId = ""
sw.addEventListener("install", async function (event) {
  channelId = await fetch(`/channel/registry`).then(res => res.text())
  event.waitUntil(sw.skipWaiting());
});

sw.addEventListener("activate", function (event) {
  event.waitUntil(sw.clients.claim());
});



// deno-lint-ignore no-explicit-any
const FETCH_EVENT_MAP = new Map<number, { event: FetchEvent, response?: Response, responseStream: ReadableStream, responseStreamController: ReadableStreamController<any> }>()

// remember event.respondWith must sync call🐰
sw.addEventListener("fetch", async (event) => {
  const request = event.request;
  const chunks = new HttpRequestBuilder(request, request.method, request.url, request.headers, request.body);
  const fetchTask = {
    event,
    response: new Response(),
    responseStream: new ReadableStream({
      start(controller) {
        fetchTask.responseStreamController = controller
      }
    }),
    responseStreamController: new ReadableStreamDefaultController,
  }
  FETCH_EVENT_MAP.set(chunks.reqId, fetchTask);
  for await (const chunk of chunks) {
    do {
      const { success } = await fetch(`/channel/${channelId}/${chunk}`).then(res => res.json(), _ => ({ success: false }));
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
    HttpRequestBuilder.REQ_ID[0] += 2;
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
    const bodyId = headerId + 1;// 奇数为body
    yield encodeToHex(headerId, JSON.stringify([this.method, this.url, this.header]));

    if (!this.body) {
      return
    }
    const reader = this.body.getReader()
    while (true) {
      const { done, value } = await reader.read()
      if (done) {
        break
      }
      console.log("serviceWorker yield =>", `${bodyId.toString().padStart(4, '0')}${value}`)
      yield `${bodyId.toString().padStart(4, '0')}${value}`
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


