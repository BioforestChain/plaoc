    import { Console } from 'bfs:console'
    import { createVM, createWorkerVM } from 'bfs:vm'

    async function linker(s) {
      if (s === '@bfs/console') { 
        return { Console }
      }
    }

    new Worker(block module{
      createVM(linker)
    })



    // block module
    import { Console } from 'bfs:console'

    const x = new Console
    new Worker(module {
      const x = new Console
    })


    // bfs-app.js
    import { DWebview } from 'xxxx'

    const webview = new DWebview();

    const workerApp = new Worker(module{
      const windows = await webview.getWindows()
      windows.document.body = "asfafsa"
      windows.document.body.height
    });


    deno -> html / js
    deno -> android -> html / js

    m1: android -> html / js // spa
    m2: bfs(close) -> android -> html / js // mpa
    m3: bfs(open) -> android -> html / js // ssr // 面向OO角色编程 分布式节点
    m4: bfs(open) -> pc -> (html / js)?
        bfs -> BNRTC/App(GUI:Web+TUI) (WebGPU+WASM)

    await Webview.useable()?
    await camera.useable("env"|"face")?

    @role("user")
    class UserZZZ {

    }
    -> BNRTC ->


    websocket -> comlink-v2 / (googlechromelab/comlink)
    @JavascriptInterface  null / string / number // -1/0/1->bool
    new WebViewMessageChannel()->secriable js-object // map/set/arraybuffer

    // main.js
    comlink.export({ foo(a, b) { return a + b } }, endpoint)


    // worker.js
    const mainModule = comlink.warp(endpoint);
    const res = await mainModule.foo(1, 1) // get foo, call foo, [1,1]

    // comlink-v2->async/sync
    Atomics.wait / notify / load

    const mainModule = comlink.warp(endpoint);
    const res = mainModule.foo(1, 1) // get foo, call foo, [1,1]
