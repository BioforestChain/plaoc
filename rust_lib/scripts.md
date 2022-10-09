## m1电脑需要配置的环境


```shell
export RUSTY_V8_ARCHIVE="$PWD/assets/rusty_v8_mirror/v0.48.1/librusty_v8_release_aarch64-linux-android.a"

export ANDROID_NDK_ROOT="/Users/bngj/Library/Android/sdk/ndk/22.0.7026061"

export PATH="$PATH:$ANDROID_NDK_ROOT/toolchains/llvm/prebuilt/darwin-x86_64/bin"

export RUSTY_V8_MIRROR="$PWD/assets/rusty_v8_mirror/"
```

### 基础rust项目命令

直接执行 main.rs

```shell
cargo run
```

编译出 android lib

```shell
rustup  target add aarch64-linux-android

RUST_BACKTRACE=full cargo build  --target=aarch64-linux-android --release

RUST_BACKTRACE=full cargo build  --target=aarch64-apple-darwin --release
```

修改默认编译器

```bash
rustup set default-host x86_64-pc-windows-msvc
rustup set default-host x86_64-pc-windows-gnu
```

### 如果遇到v8报错需要设置环境变量

```powershell
$env:RUSTY_V8_MIRROR="%PWD%\assets\rusty_v8_mirror\"
$env:RUSTY_V8_ARCHIVE="%PWD%\assets\rusty_v8_mirror\v0.48.1\rusty_v8_release_x86_64-pc-windows-msvc.lib"
$env:RUSTY_V8_ARCHIVE="%PWD%\assets\rusty_v8_mirror\v0.48.1\librusty_v8_release_aarch64-linux-android.a"
```

```cmd
set RUSTY_V8_MIRROR=$PWD\assets\rusty_v8_mirror\
set RUSTY_V8_ARCHIVE=$PWD\assets\rusty_v8_mirror\v0.48.1\rusty_v8_release_x86_64-pc-windows-msvc.lib
```

```bash
export RUSTY_V8_MIRROR="$PWD/assets/rusty_v8_mirror/"

export RUSTY_V8_ARCHIVE="$PWD/assets/rusty_v8_mirror/v0.48.1/librusty_v8_release_aarch64-linux-android.a"
export RUSTY_V8_ARCHIVE="$PWD/assets/rusty_v8_mirror/v0.48.1/librusty_v8_release_aarch64-unknown-linux-gnu.a"
export RUSTY_V8_ARCHIVE="$PWD/assets/rusty_v8_mirror/v0.48.1/librusty_v8_release_aarch64-apple-darwin.a"
```

### 修复`unable to find library -lgcc`的问题

网上有一种错误说法，其实只能让编译通过，实际是运行的时候会爆出错误：

```bash
cd /Users/kzf/Library/Android/sdk/ndk/24.0.8215888/toolchains/llvm/prebuilt/darwin-x86_64/lib64/clang/14.0.1/lib/linux/aarch64
echo "INPUT(-lunwind)" > libgcc.a

java.lang.UnsatisfiedLinkError: dlopen failed: cannot locate symbol "__emutls_get_address" referenced by "/data/app/~~xgQux0SWdH8NR7GLHyXCNg==/org.bfchain.rust.example-1rL1uIoeTHAxKOyHiDM32w==/base.apk!/lib/arm64-v8a/librust_lib.so"...
```

目前已知的解决方法就是把ndk锁在22版本以下

### 修复 failed to run custom build command for `ring v0.16.20`

运行

```bash
RUST_BACKTRACE=1 cargo build --target=aarch64-linux-android --release
```

发现

```bash
 --- stderr

  error occurred: Failed to find tool. Is `aarch64-linux-android-clang` installed?
```

原因是环境变量的名称错误，需要更改一下

```bash
# 把 aarch64-linux-android-clang 加入环境变量
export PATH="$PATH:$ANDROID_NDK_ROOT/toolchains/llvm/prebuilt/darwin-x86_64/bin"
# PS: $ANDROID_NDK_ROOT 大概是："/Users/kzf/Library/Android/sdk/ndk/22.0.7026061" 需要代码NDK版本号

# mac确保 aarch64-linux-android-clang 存在
cd $ANDROID_NDK_ROOT/toolchains/llvm/prebuilt/darwin-x86_64/bin/
cp aarch64-linux-android30-clang aarch64-linux-android-clang

# mac确保 clang 存在,或者指定export CC="$ANDROID_NDK_ROOT/toolchains/llvm/prebuilt/darwin-x86_64/bin/aarch64-linux-android30-clang"
cd $ANDROID_NDK_ROOT/toolchains/llvm/prebuilt/darwin-x86_64/bin/
cp aarch64-linux-android30-clang clang
```

### 修复error: failed to run custom build command for `libffi-sys v1.3.2`

```bash
brew install autoconf automake libtool
```

然后 在ext/ffi/cargo.toml
修改版本为：libffi = "3.0.0"

### thread 'main' panicked at 'called `Result::unwrap()` on an `Err` value: Os { code: 2, kind: NotFound, message: "No such file or directory" }', /Users/waterbang/.cargo/registry/src/github.com-1ecc6299db9ec823/v8-0.48.1/build.rs:366:30

```bash
export RUSTY_V8_ARCHIVE="$PWD/assets/rusty_v8_mirror/v0.48.1/librusty_v8_release_aarch64-linux-android.a"
```

### ld: symbol(s) not found for architecture x86_64

确保以下文件存在
  > rust_lib/assets/rusty_v8_mirror/v0.48.1/librusty_v8_release_aarch64-linux-android.a
  > rust_lib/assets/rusty_v8_mirror/v0.48.1/librusty_v8_release_x86_64-apple-darwin.a

```bash
 brew install glib glib-utils
```

### error: failed to add native library /Users/mac/Desktop/waterbang/project/plaoc/rust_lib/target/aarch64-linux-android/release/gn_out/obj/librusty_v8.a: file too small to be an archive

```bash
export RUSTY_V8_MIRROR="$PWD/assets/rusty_v8_mirror/"
```

### Relocations in generic ELF (EM: 183)/usr/bin/ld: /plaoc/rust_lib/target/release/deps/libv8-43d0f1725d683f0e.rlib: error adding symbols: file in wrong format  collect2: error: ld returned 1 exit status

注意这里使用了错误的链接器

```bash
unset RUSTY_V8_ARCHIVE
```

### link CC error: could not compile `deno_graph` due to previous error

```bash
 [target.x86_64-linux-android]
   ar = "/Users/waterbang/Library/Android/sdk/ndk/22.1.7171670/toolchains/llvm/prebuilt/darwin-x86_64/bin/x86_64-linux-android-ar"
   linker = "/Users/waterbang/Library/Android/sdk/ndk/22.1.7171670/toolchains/llvm/prebuilt/darwin-x86_64/bin/x86_64-linux-android28-clang++"
```
