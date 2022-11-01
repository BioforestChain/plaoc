/// <reference lib="webworker" />

const sw = self as unknown as ServiceWorkerGlobalScope;
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

const FETCH_EVENT_MAP = new Map<string,
  {
    event: FetchEvent, response: Response, responseStream: ReadableStream,
    responseStreamController: ReadableStreamController<ArrayBuffer> | null
  }>()

// remember event.respondWith must sync call🐰
sw.addEventListener("fetch", (event) => {
  const request = event.request.clone();

  const path = new URL(request.url).pathname
  // 资源文件不处理
  if (path.lastIndexOf(".") !== -1) {
    return
  }

  let responseStreamController: ReadableStreamController<ArrayBuffer> | null = null;
  // 生成结构体
  const fetchTask = {
    event: new FetchEvent("fetch", event),
    response: new Response(),
    responseStream: new ReadableStream({
      start(controller) {
        responseStreamController = controller
      }
    }),
    responseStreamController,
  }
  // 存起来
  FETCH_EVENT_MAP.set(String(channelId), fetchTask);

  handleRequest(request)

});



async function handleRequest(request: Request) {
  // 如果id为空需要申请id
  if (channelId === "") {
    channelId = await registerChannel()
  }
  // console.log(`HttpRequestBuilder ${JSON.stringify(await request.text())},url: ${JSON.stringify(await request.arrayBuffer())}`)
  console.log(`HttpRequestBuilder ${request.method},url: ${request.url},headers: ${request.headers},body: ${request.body}`)

  // Build chunks
  const chunks = new HttpRequestBuilder(request, request.method, request.url, request.headers as unknown as Record<string, string>, request.body);

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
}

const encodeToHex = (reqId: number, data: string) => {
  return `${reqId.toString().padStart(4, '0')}:${hexEncode(data)}`
}


export class HttpRequestBuilder {
  static REQ_ID = new Uint16Array(1);
  static BODY_ID = new Uint16Array(1);
  static getReqId() {
    const reqId = HttpRequestBuilder.REQ_ID[0]
    HttpRequestBuilder.REQ_ID[0] += 2; // 0, 2 ,4,6
    return reqId;
  }
  static getBodyId(reqId: number) {
    const bodyId = HttpRequestBuilder.BODY_ID[0];
    HttpRequestBuilder.BODY_ID[0] = reqId + 1;// 1,3,5,7
    return bodyId;
  }
  readonly reqId = HttpRequestBuilder.getReqId()
  readonly bodyId = HttpRequestBuilder.getBodyId(this.reqId)

  constructor(
    readonly request: Request,
    readonly method: string,
    readonly url: string,
    readonly header: Record<string, string>,
    readonly body: ReadableStream<Uint8Array> | null
  ) { }


  async *[Symbol.asyncIterator]() {
    const headerId = this.reqId;// 偶数为头
    const bodyId = this.bodyId;// 奇数为body
    console.log("headerId:", headerId, "bodyId:", bodyId)
    // 只传递body
    yield encodeToHex(headerId, `${this.url}|${JSON.stringify(this.header)}`);
    // 如果body为空
    if (!this.body) {
      return
    }
    const reader = this.body.getReader()
    while (true) {
      const { done, value } = await reader.read()
      if (done) {
        yield `${bodyId.toString().padStart(4, '0')}:${done}` // 消息结束
        break
      }

      yield `${bodyId.toString().padStart(4, '0')}:${value}`
    }
  }
}

// return data 🐯
sw.addEventListener('message', event => {
  if (typeof event.data !== 'string') return
  const [channelId, end, dataHex] = String(event.data).split(':');
  const chunk = String(dataHex).split(",") as any;
  console.log(`serviceWorker chunk=> ${chunk},end:${end}`);
  const fetchTask = FETCH_EVENT_MAP.get(channelId);
  // 如果存在
  if (fetchTask) {
    console.log("如果存在:", end, hexDecode(chunk))
    // body reqId为偶数
    if (end == "false" && fetchTask.responseStreamController !== null) {
      console.log(`填入数据=> ${chunk}`);
      fetchTask.responseStreamController.enqueue(chunk);
      return
    }
    const data = hexDecode(chunk);
    console.log(`请求结束返回数据=> ${data}`);
    const [headers, status, statusText] = data.split(",");
    console.log("解析headers", headers, status, statusText)
    fetchTask.response = new Response(fetchTask.responseStream, { headers: {}, status: Number(status), statusText })
    fetchTask.event.respondWith(fetchTask.response)
  }
})


function hexEncode(data: string) {
  const encoder = new TextEncoder();
  return encoder.encode(data);
}

function hexDecode(buffer: ArrayBuffer) {
  return new TextDecoder().decode(new Uint8Array(buffer));
}

/**
 * 等待函数
 * @param delay
 * @returns
 */
const sleep = (delay: number) =>
  new Promise((resolve) => setTimeout(resolve, delay));


