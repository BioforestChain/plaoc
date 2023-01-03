# bfs 基础操作系统运行时

## 目标

1. 为了解决现代单页应用（SPA）性能不足的问题。

2. 为开发者提供android、ios、桌面应用开发。

3. 为BFS资产的面向角色的编程和开发做准备，为区块链开发搭建社区基础设施。

## 快速开始

见文档：<https://docs.plaoc.com>

### 项目流程

    dwebView-js-(fetch)->kotlin-(ffi)->rust-(op)->deno-js
    deno-js-(op)->rust-(ffi)->kotlin-(evaljs)->dwebView-js

## 开发者指南

首先需要构建一个动态链接库。

### 构建动态链接库

> waring 此处需要使用arm64架构的电脑构建，如苹果的M1芯片电脑。

#### 在容器中如何在linux/amd64架构下构建arm64 架构的动态链接库

下面的命令会起一个容器不要杀掉。

```bash
docker run --rm --privileged multiarch/qemu-user-static --reset -p yes
docker buildx create --name multiarch --driver docker-container --use
docker buildx inspect --bootstrap
```

#### docker编译so

```bash
# 如果不自己构建可以不运行这一句
docker buildx build --platform linux/arm64  -t waterbang/aarch64-linux-android:arm-ndk25-rust1.63.0 .
# 在plaoc根目录执行
docker run -it -v /Users/mac/Desktop/waterbang/project/plaoc/qemu-aarch64-static:/usr/bin/qemu-aarch64-static -v $(pwd):/plaoc  waterbang/aarch64-linux-android:arm-ndk25-rust1.63.0  /bin/bash

cd /plaoc/rust_lib

RUST_BACKTRACE=1 cargo build -vv --target=aarch64-linux-android --release
```

> qemu在此处有bug，会吃掉虚拟机的所有内存和cpu。

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

API见文档：<https://docs.plaoc.com>

#### 后端包 @bfsx/core,@bfsx/metadata

API见文档：<https://docs.plaoc.com>

### 如果修改了rust代码，需要重新编译

首先要保证一下文件存在,点击下载[librusty_v8_release_aarch64-linux-android.a](https://github.com/waterbang/rusty_v8/releases),
[librusty_v8_release_x86_64-apple-darwin.a](https://github.com/denoland/rusty_v8/releases)

> 1. rust_lib/assets/rusty_v8_mirror/v0.48.1/librusty_v8_release_aarch64-linux-android.a
> 2. rust_lib/assets/rusty_v8_mirror/v0.48.1/librusty_v8_release_x86_64-apple-darwin.a

```bash
cd rust_lib

cargo build --target=aarch64-linux-android --release
```

### android

查看rust_lib/README.md

直接在android上运行，以上可能有路径问题，记得切换为自己的路径，deno-rumtime 遇到问题，详情查看 `/rust_lib/scripts.md`。
或者[这里](https://www.waterbang.top/2022/08/08/BFS%E5%BC%80%E5%8F%91%E9%97%AE%E9%A2%98%E6%B1%87%E6%80%BB/).

### ios

### desktop

### 目前规划

    [😃] 前端和后端使用二进制互传数据
    [😃] 修改 webComponent 的 javascriptinterface 注入的方式
    [😃] 封装.bfsa 文件，实现应用安装到设备。
    [🥳] 封装系统级服务，编写基础测试代码。
    [🥳] 封装文件系统，权限系统和网络系统
    [🥳] 规范多个 DwebView 之间数据互动的标准
    [🥳] 规范多个 后端服务（deno-js）的数据互动标准
    [❌] 迁移碳元域
    [❌] 优化代码的封装，契合面向角色编程。
    [❌] 编写单元测试代码，覆盖率需要达到90%。
