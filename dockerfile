FROM saschpe/android-ndk:jdk11_api31_30.0.3_ndk21.4.7075529_cmake3.18.1

LABEL author "BFChain"

ENV DEBIAN_FRONTEND=noninteractive \
  ANDROID_NDK_ROOT="/opt/android-sdk-linux/ndk/21.4.7075529" \ 
  PATH="$PATH:/opt/android-sdk-linux/ndk/21.4.7075529/toolchains/llvm/prebuilt/linux-x86_64/bin:/root/.cargo/bin" \
  RUSTY_V8_MIRROR="/plaoc/rust_lib/assets/rusty_v8_mirror/" 

# RUSTY_V8_ARCHIVE="/plaoc/rust_lib/assets/rusty_v8_mirror/v0.48.1/librusty_v8_release_aarch64-linux-android.a"

# NDK相关环境配置
# RUN mkdir -p /opt/android/ndk &&  cd /opt/android/ndk
# RUN wget https://dl.google.com/android/repository/android-ndk-r22b-linux-x86_64.zip && unzip /android-ndk-r22b-linux-x86_64.zip

# rust 
RUN apt-get update  && \
  apt-get install curl autoconf libtool automake -y && \
  cd $ANDROID_NDK_ROOT/toolchains/llvm/prebuilt/linux-x86_64/bin/ && \
  cp aarch64-linux-android30-clang aarch64-linux-android-clang && \
  curl https://sh.rustup.rs -sSf | \
  sh -s -- --default-toolchain stable -y

RUN rustup  target add aarch64-linux-android

RUN echo '[target.aarch64-linux-android]\n'\
  'ar = "/opt/android-sdk-linux/ndk/21.4.7075529/toolchains/llvm/prebuilt/linux-x86_64/bin/aarch64-linux-android-ar"\n'\
  'linker = "/opt/android-sdk-linux/ndk/21.4.7075529/toolchains/llvm/prebuilt/linux-x86_64/bin/aarch64-linux-android28-clang++"\n'\
  '[target.armv7-linux-androideabi]\n'\
  'linker = "/opt/android-sdk-linux/ndk/21.4.7075529/toolchains/llvm/prebuilt/linux-x86_64/bin/armv7a-linux-androideabi28-clang++"\n'\
  '[target.x86_64-linux-android]\n'\
  'linker = "/opt/android-sdk-linux/ndk/21.4.7075529/toolchains/llvm/prebuilt/linux-x86_64/bin/x86_64-linux-android28-clang++"\n'\
  > ~/.cargo/config.toml


#  接下来 cd /plaoc/rust_lib
#  执行 RUST_BACKTRACE=1 cargo build -vv --target=aarch64-linux-android --release


# 如果要构建新镜像 docker build -t waterbang/aarch64-linux-android:arm-ndk21-rust1.63.0 .
