/// <reference lib="webworker" />

const sw = self as unknown as ServiceWorkerGlobalScope;
const responseList: Response[] = [];

sw.addEventListener("install", function (event) {
  event.waitUntil(sw.skipWaiting());
});

sw.addEventListener("activate", function (event) {
  event.waitUntil(sw.clients.claim());
});

// remember event.respondWith must sync call🐰
sw.addEventListener("fetch", function (event) {
  const request = event.request;
  // POST
  if (request.method.match(/POST/i)) {
    handleRequest(event);
    return;
  }
  // GET
  event.respondWith(
    async function () {
      // event.request.headers["Range"] = "0-160"
      return await fetch(request);
    }(),
  );
});

function handleRequest(event: FetchEvent) {
  event.respondWith(
    async function () {
      await postFactory(event.request, event); // 发送请求
      const reader = iterResponse();
      const { value, done } = await reader.next();
      return value;
    }(),
  );
}

/**迭代器 */
function iterResponse() {
  return {
    next: () => {
      const data = responseList.shift();
      if (data) {
        return {
          value: data,
          done: false,
        };
      }
      return { value: new Response(), done: true };
    },
  };
}

/**
 * post处理
 * @param request
 * @returns
 */
async function postFactory(request: Request, event: FetchEvent) {
  if (request.body === null) return;

  const file = await request.arrayBuffer();

  const bufferList = fileChunk(new Uint8Array(file));
  const warpRes: string[] = [];
  // 等待
  await Promise.all(bufferList.map(async (value) => {
    const res = await getFactory(event, value);
    warpRes.push(await res.text());
  }));
  responseList.push(new Response(String(warpRes)));
}
/**
 * get处理
 * @param event
 * @param dataBuffer
 */
async function getFactory(event: FetchEvent, dataBuffer: Uint8Array) {
  const request = event.request;
  const response = await fetch(`${request.url}?upload=${dataBuffer}`, {
    headers: request.headers,
    method: "GET",
    mode: "cors",
  });
  return response;
}
/** 根据file.size进行循环切割. */
function fileChunk(fileBuff: Uint8Array) {
  let index = 0;
  const oneM = 1024 * 512 * 1; // 不要再增大了，native端处理不来
  const bufferList = [];
  do {
    bufferList.push(fileBuff.subarray(index, index + oneM));
    index += oneM;
  } while (fileBuff.byteLength > index);
  return bufferList;
}

// 不要删除，serverWorker没有window对象
(self as any).export = "";
export { };
