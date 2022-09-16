
/// <reference lib="webworker" />

const sw = self as unknown as ServiceWorkerGlobalScope;

sw.addEventListener('install', function (event) {
  console.log("serverWorker: install")
  event.waitUntil(sw.skipWaiting());
});

sw.addEventListener('activate', function (event) {
  console.log('serverWorker: activate!', event);
  event.waitUntil(sw.clients.claim());
});

sw.addEventListener('fetch', function (event) {
  console.log("serverWorker.fetch :", event.request.url);
  event.respondWith(
    async function () {
      return fetch(event.request);
    }()
  );
});


// 不要删除，serverWorker没有window对象
(self as any).export = ""

export { }
