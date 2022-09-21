RUST_BACKTRACE=1 cargo build  --target=aarch64-linux-android --release
node copy-to-android.cjs
