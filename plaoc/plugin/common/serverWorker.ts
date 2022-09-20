
/// <reference lib="webworker" />

const sw = self as unknown as ServiceWorkerGlobalScope;

sw.addEventListener('install', function (event) {
  event.waitUntil(sw.skipWaiting());
});

sw.addEventListener('activate', function (event) {
  event.waitUntil(sw.clients.claim());
});

sw.addEventListener('fetch', function (event) {
  const request = event.request;
  if (request.method.match(/POST/i)) {
    return postFactory(request)
  }
  return getFactory(event)
});
/**
 * post处理
 * @param request 
 * @returns 
 */
async function postFactory(request: Request) {
  if (request.body === null) return;

  const file = await request.arrayBuffer();

  const reader = new FileReader();
  reader.readAsArrayBuffer(new Blob([file]));

  reader.addEventListener("load", async (e) => {
    const fileBuff = new Uint8Array(e.target!.result! as ArrayBuffer);

    const bufferList = fileChunk(fileBuff)
    console.log("serverWorker.postFactory arrayBuffer:", bufferList);
  });
}

function getFactory(event: FetchEvent) {
  event.respondWith(
    async function () {
      // event.request.headers["Range"] = "0-160"
      return fetch(event.request);

    }()
  );
}
//根据file.size进行循环切割.
function fileChunk(fileBuff: Uint8Array) {
  let index = 0;
  let oneM = 1024 * 1024 * 1;
  const bufferList = [];
  do {
    bufferList.push(fileBuff.subarray(index, index + oneM));
    index += oneM;
  } while (fileBuff.byteLength > index)
  return bufferList
}

// 不要删除，serverWorker没有window对象
(self as any).export = ""
export { }
