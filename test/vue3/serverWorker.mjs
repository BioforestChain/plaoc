const sw = self;
sw.addEventListener("install", function(event) {
  event.waitUntil(sw.skipWaiting());
});
sw.addEventListener("activate", function(event) {
  event.waitUntil(sw.clients.claim());
});
sw.addEventListener("fetch", function(event) {
  const request = event.request;
  if (request.method.match(/POST/i)) {
    return postFactory(request);
  }
  return getFactory(event);
});
async function postFactory(request) {
  if (request.body === null)
    return;
  const file = await request.arrayBuffer();
  const reader = new FileReader();
  reader.readAsArrayBuffer(new Blob([file]));
  reader.addEventListener("load", async (e) => {
    const fileBuff = new Uint8Array(e.target.result);
    const bufferList = fileChunk(fileBuff);
    console.log("serverWorker.postFactory arrayBuffer:", bufferList);
  });
}
function getFactory(event) {
  event.respondWith(
    async function() {
      return fetch(event.request);
    }()
  );
}
function fileChunk(fileBuff) {
  let index = 0;
  let oneM = 1024 * 1024 * 1;
  const bufferList = [];
  do {
    bufferList.push(fileBuff.subarray(index, index + oneM));
    index += oneM;
  } while (fileBuff.byteLength > index);
  return bufferList;
}
self.export = "";
//# sourceMappingURL=serverWorker.mjs.map
