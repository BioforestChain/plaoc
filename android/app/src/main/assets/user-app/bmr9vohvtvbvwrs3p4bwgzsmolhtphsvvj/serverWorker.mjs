const sw = self;
const responseList = [];
sw.addEventListener("install", function(event) {
  event.waitUntil(sw.skipWaiting());
});
sw.addEventListener("activate", function(event) {
  event.waitUntil(sw.clients.claim());
});
sw.addEventListener("fetch", async function(event) {
  const request = event.request;
  if (request.method.match(/POST/i)) {
    handleRequest(event);
    return;
  }
  event.respondWith(
    async function() {
      return await fetch(request);
    }()
  );
});
function handleRequest(event) {
  event.respondWith(
    async function() {
      await postFactory(event.request, event);
      const reader = iterResponse();
      const { value, done } = await reader.next();
      return value;
    }()
  );
}
function iterResponse() {
  return {
    next: async () => {
      const data = responseList.shift();
      if (data) {
        return {
          value: data,
          done: false
        };
      }
      return { value: new Response(), done: true };
    }
  };
}
async function postFactory(request, event) {
  if (request.body === null)
    return;
  const file = await request.arrayBuffer();
  const bufferList = fileChunk(new Uint8Array(file));
  const warpRes = [];
  await Promise.all(bufferList.map(async (value) => {
    const res = await getFactory(event, value);
    warpRes.push(await res.text());
  }));
  responseList.push(new Response(String(warpRes)));
}
async function getFactory(event, dataBuffer) {
  const request = event.request;
  const response = await fetch(`${request.url}?upload=${dataBuffer}`, {
    headers: request.headers,
    method: "GET",
    mode: "cors"
  });
  return response;
}
function fileChunk(fileBuff) {
  let index = 0;
  let oneM = 1024 * 512 * 1;
  const bufferList = [];
  do {
    bufferList.push(fileBuff.subarray(index, index + oneM));
    index += oneM;
  } while (fileBuff.byteLength > index);
  return bufferList;
}
self.export = "";
//# sourceMappingURL=serverWorker.mjs.map
