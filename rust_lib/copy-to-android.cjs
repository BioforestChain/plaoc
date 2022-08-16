const fs = require("node:fs");
const path = require("node:path");
const resolteTo = (to) => path.join(__dirname, to);

fs.copyFileSync(
  resolteTo("./target/aarch64-linux-android/release/librust_lib.so"),
  resolteTo("../android/app/src/main/libs/arm64-v8a/librust_lib.so")
);
