## TODO

- [ ] 未捕捉的异常不要恐慌
- [x] Worker 的支持
- [ ] FS-API 的支持（转录到应用内部存储）

## 编译环境配置流程

1. 下载它们两个

  > rust_lib/assets/rusty_v8_mirror/v0.48.1/librusty_v8_release_aarch64-linux-android.a
  > rust_lib/assets/rusty_v8_mirror/v0.48.1/librusty_v8_release_x86_64-apple-darwin.a

1. 添加变量：export RUSTY_V8_MIRROR="$PWD/assets/rusty_v8_mirror/"

2. 把 aarch64-linux-android-clang 加入环境变量

  ```bash
  # $ANDROID_NDK_ROOT 大概是："/Users/kzf/Library/Android/sdk/ndk/22.0.7026061" 需要代码NDK版本号

  export PATH="$PATH:$ANDROID_NDK_ROOT/toolchains/llvm/prebuilt/darwin-x86_64/bin"
  ```

3. 确保 aarch64-linux-android-clang 存在

  ```bash
  cd $ANDROID_NDK_ROOT/toolchains/llvm/prebuilt/darwin-x86_64/bin/
  cp aarch64-linux-android30-clang aarch64-linux-android-clang 
  ```
