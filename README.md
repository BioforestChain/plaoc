# bfs base runtime app for modern os

## main purpose

1. In order to solve the problem of insufficient performance of modern single page application (SPA).
2. Provide developers with application development on android, ios and desktop.
3. In order to prepare for role-oriented programming and development of BFS assets, and to build community infrastructure for blockchain development.

## 项目流程

    dwebView-js-(fetch)->kotlin-(ffi)->rust-(op)->deno-js
    deno-js-(op)->rust-(ffi)->kotlin-(evaljs)->dwebView-js

## 如何开发

## docker编译

```shell
# 在plaoc根目录执行
docker run -it -v $(pwd):/plaoc waterbang/aarch64-linux-android:arm-ndk21-rust1.60.0 /bin/bash

cd /plaoc/rust_lib

./build-android.sh

```

### 测试（现在只提供vue3）

如何测试

```bash

# 前端
cd test-vue3

yarn install

# 后端 
cd test-vue3/bfs-service

bfsp init

bfsp dev

# 前端上传到android
yarn build

# 后端上传到android
node test-vue3/bfs-service/copy.cjs

#后续封装成.bfsa
```

### plaoc

BFS 启动

```bash
cd plaoc

bfsw init

bfsw dev
```

#### 前端包 @bfsx/plugin

#### 后端包 @bfsx/core,@bfsx/metadata,@bfsx/plugin

### deno-runtime

首先要保证一下文件存在：

1. rust_lib/assets/rusty_v8_mirror/v0.48.1/librusty_v8_release_aarch64-linux-android.a
2. rust_lib/assets/rusty_v8_mirror/v0.48.1/librusty_v8_release_x86_64-apple-darwin.a

```bash
cd rust_lib

cargo build --target=aarch64-linux-android --release

cross build --target=aarch64-linux-android --release
```

### android

查看rust_lib/README.md

直接在android上运行，以上可能有路径问题，记得切换为自己的路径，deno-rumtime 遇到问题，详情查看 `/rust_lib/scripts.md`。
或者[这里](https://www.waterbang.top/2022/08/08/BFS%E5%BC%80%E5%8F%91%E9%97%AE%E9%A2%98%E6%B1%87%E6%80%BB/).

### ios

### desktop

### 目前规划

    [😃] 前端和后端使用二进制互传数据
    [❌] 修改 webComponent 的 javascriptinterface 注入的方式
    [❌] 封装系统级服务，编写基础测试代码。
    [❌] 封装文件系统，权限系统和网络系统
    [❌] 规范多个 DwebView 之间数据互动的标准
    [❌] 规范多个 后端服务（deno-js）的数据互动标准
    [❌] 迁移碳元域
    [❌] 封装.bfsa 文件，实现应用安装到设备。
    [❌] 优化代码的封装，契合面向角色编程。
    [❌] 编写单元测试代码，覆盖率需要达到90%。
