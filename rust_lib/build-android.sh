RUST_BACKTRACE=1 cargo build  --target=aarch64-linux-android --release

scp ./target/aarch64-linux-android/release/librust_lib.so mac@172.30.90.64:/Users/mac/Desktop/waterbang/project/plaoc/android/app/src/main/libs/arm64-v8a/
#node copy-to-android.cjs
