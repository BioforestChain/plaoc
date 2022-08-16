### 基础rust项目命令

直接执行 main.rs

```shell
cargo run
```

编译出 android lib

```shell
rustup  target add aarch64-linux-android

RUST_BACKTRACE=full cargo build -vv --target=aarch64-linux-android --release

RUST_BACKTRACE=full cargo build -vv --target=aarch64-apple-darwin --release
```

修改默认编译器

```
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

# 确保 aarch64-linux-android-clang 存在
cd $ANDROID_NDK_ROOT/toolchains/llvm/prebuilt/darwin-x86_64/bin/
cp aarch64-linux-android30-clang aarch64-linux-android-clang 
```

### 修复error: failed to run custom build command for `libffi-sys v1.3.2`

```
brew install autoconf automake libtool   
```

然后 在ext/ffi/cargo.toml
修改版本为：libffi = "3.0.0"
