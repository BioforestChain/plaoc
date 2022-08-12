# bfs base runtime app for modern os (This project is to be migrated, stop updating here)

>此项目已停止更新

## main purpose

1. In order to solve the problem of insufficient performance of modern single page application (SPA).
2. Provide developers with application development on android, ios and desktop.
3. In order to prepare for role-oriented programming and development of BFS assets, and to build community infrastructure for blockchain development.

## 项目流程

dwebView-js-(fetch)->kotlin-(ffi)->rust-(op)->deno-js
deno-js-(op)->rust-(ffi)->kotlin-(evaljs)->dwebView-js

## BFS

    core模块先不考虑，先直接都写到runtime里面。
    runtime怎么写？
    网关怎么封装？

### js传递给deno数据，deno到kotlin（所有内容都采用二进制）

    [😃] 直接采用内存交互的方式
    [😃] 把FFI封装为事件总线
    [😃 ] 传递大数据量的话，参考网络层发包存包的形式，去接收二进制数据

 直接使用JSON序列化,如果遇到大文件直接传递内存地址就可以。

### 目前规划

    [😃] 封装webComponent
    [😃] 把js调deno的webSocket注入webComponent
    [😃] 打通js传递消息到deno
    [😃] deno回传给js消息
    [😃] deno需要携带消息给kotlin,不仅仅是调用一个方法
    [😃] bfs移到deno里面
    [😃] 使用FFI和op的方式，代替websocket在[bfs,rust,kotlin]之间传递消息
    [😃] 封装为事件总线（ip包头的封装方法，使用二进制去区分）
    [😃] 重新正确的规划整个plugin和runtime模块，需要和小伙伴讨论。
    [😃] 系统的封装完android的方法，把函数都暴露出来。
