const fs = require('fs');
const path = require("path");

/*
 * 复制目录、子目录，及其中的文件
 * @param src {String} 要复制的目录
 * @param dist {String} 复制到目标目录
 */
function createSymlink(src, dist) {
  if(fs.existsSync(dist)) {
    try {
      fs.unlinkSync(dist);
    } catch(e) {
      console.log(e);

      let options = { force: true, recursive: true };
      if(dist.endsWith(".mjs")) {
        options = { force: true };
      }

      fs.rmSync(dist, options);
    }
  }

  fs.symlinkSync(src, dist);
  console.log("创建成功");
  return;
}

// 创建test-vue3链接到android项目
const sourcePath = path.join(__dirname, "./build");
const targetPath = path.join(__dirname, "../android/app/src/main/assets");

createSymlink(sourcePath, targetPath);


// 创建serverWorker链接到android项目
const workerSrcPath = path.join(__dirname, "./node_modules/@bfsx/plugin/dist/default/esm/common/serverWorker.mjs");
const workerDistPath = path.join(__dirname, "../android/app/src/main/assets/serverWorker.mjs");

createSymlink(workerSrcPath, workerDistPath);

