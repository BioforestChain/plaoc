FROM saschpe/android-ndk:jdk11_api31_30.0.3_ndk21.4.7075529_cmake3.18.1

ARG NDK_VERSION="21.4.7075529"

LABEL author "BFChain"

# RUN mkdir -p /opt/android-sdk-linux/ndk &&  cd /opt/android-sdk-linux/ndk

# NDK相关环境配置
# RUN wget https://dl.google.com/android/repository/${NDK_VERSION}-linux-x86_64.zip && \
#   unzip /${NDK_VERSION}-linux-x86_64.zip && \
#   rm /${NDK_VERSION}-linux-x86_64.zip

# Add ndk-build to the search path
ENV DEBIAN_FRONTEND=noninteractive \
  ANDROID_NDK_ROOT="/opt/android-sdk-linux/ndk/${NDK_VERSION}" \ 
  PATH="$PATH:/opt/android-sdk-linux/ndk/${NDK_VERSION}/toolchains/llvm/prebuilt/linux-x86_64/bin:/root/.cargo/bin" \
  RUSTY_V8_MIRROR="/plaoc/rust_lib/assets/rusty_v8_mirror/" \
  RUSTY_V8_ARCHIVE="/plaoc/rust_lib/assets/rusty_v8_mirror/v0.48.1/librusty_v8_release_aarch64-linux-android.a"



# rust 
RUN apt-get update && \
  apt-get install curl autoconf libtool automake -y && \
  cd $ANDROID_NDK_ROOT/toolchains/llvm/prebuilt/linux-x86_64/bin/ && \
  cp aarch64-linux-android30-clang aarch64-linux-android-clang && \
  curl https://sh.rustup.rs -sSf | \
  sh -s -- --default-toolchain stable -y

RUN rustup  target add aarch64-linux-android

RUN echo '[target.aarch64-linux-android]\n'\
  'ar = "/opt/android-sdk-linux/ndk/${NDK_VERSION}/toolchains/llvm/prebuilt/linux-x86_64/bin/aarch64-linux-android-ar"\n'\
  'linker = "/opt/android-sdk-linux/ndk/${NDK_VERSION}/toolchains/llvm/prebuilt/linux-x86_64/bin/aarch64-linux-android28-clang++"\n'\
  '[target.armv7-linux-androideabi]\n'\
  'linker = "/opt/android-sdk-linux/ndk/${NDK_VERSION}/toolchains/llvm/prebuilt/linux-x86_64/bin/armv7a-linux-androideabi28-clang++"\n'\
  '[target.x86_64-linux-android]\n'\
  'linker = "/opt/android-sdk-linux/ndk/${NDK_VERSION}/toolchains/llvm/prebuilt/linux-x86_64/bin/x86_64-linux-android28-clang++"\n'\
  > ~/.cargo/config.toml





# docker 多cpu架构支持
# docker buildx ls
# docker buildx  create --name mybuilder
# docker buildx use mybuilder  或者直接写  docker buildx use default
# docker buildx inspect --bootstrap

# 如何在linux/amd64构建arm64
#docker run --rm --privileged multiarch/qemu-user-static --reset -p yes
#docker buildx create --name multiarch --driver docker-container --use
#docker buildx inspect --bootstrap

# 如果要构建新镜像  docker build -t waterbang/aarch64-linux-android:arm-ndk21-rust1.63.0 .
#ndk21 docker buildx build --platform linux/arm64 -t waterbang/aarch64-linux-android:arm-ndk21-rust1.63.0  --push .
#ndk25 docker buildx build --platform linux/amd64,linux/arm64,linux/arm/v7  -t waterbang/aarch64-linux-android:arm-ndk25-rust1.63.0 --push --output=type=docker .

#docker run --rm -it -v /Users/mac/Desktop/waterbang/project/plaoc/qemu-aarch64-static:/usr/bin/qemu-aarch64-static waterbang/aarch64-linux-android:arm-ndk25-rust1.63.0  /bin/bash

#  接下来 cd /plaoc/rust_lib
#  执行 RUST_BACKTRACE=1 cargo build -vv --target=aarch64-linux-android --release
