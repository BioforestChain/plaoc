
/// <reference lib="webworker" />

const sw = self as unknown as ServiceWorkerGlobalScope;

sw.addEventListener('fetch', function (event) {
  console.log("111fetch:", event)
  event.respondWith(
    (async function () {

      return fetch(event.request);
    })()
  );
});

export function init() {
  console.log("init")
}


export { }
