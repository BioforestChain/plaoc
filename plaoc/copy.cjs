
// const path = require('path')
// const fs = require('fs')

// const isExist = (path) => { // 判断文件夹是否存在, 不存在创建一个
//   if (!fs.existsSync(path)) {
//     fs.mkdirSync(path)
//   }
// }

// const copyFile = (sourcePath, targetPath) => {
//   const sourceFile = fs.readdirSync(sourcePath, { withFileTypes: true })

//   sourceFile.forEach(file => {
//     const newSourcePath = path.resolve(sourcePath, file.name)
//     const newTargetPath = path.resolve(targetPath, file.name)
//     if (file.isDirectory()) {
//       isExist(newTargetPath)
//       copyFile(newSourcePath, newTargetPath)
//     }
//     if (file.name.endsWith('.mjs')) { // 需要复制其他的格式的文件修改 .mp4 既可
//       fs.copyFileSync(newSourcePath, newTargetPath)
//     }
//   })
// }

const fs = require('fs');
const path = require("path");

/*
 * 复制目录、子目录，及其中的文件
 * @param src {String} 要复制的目录
 * @param dist {String} 复制到目标目录
 */
function createSymlink(src, dist) {
  // if(!fs.existsSync(src)) {
  //   console.log("不存在要复制的目录");
  // }

  // // 不存在目标目录时，创建目录
  // if(!fs.existsSync(dist)) {
  //   fs.mkdirSync(dist, { recursive: true });
  // }

  if(fs.existsSync(dist)) {
    try {
      fs.unlinkSync(dist);
    } catch(e) {
      console.log(e);
      fs.rmdirSync(dist);
    }
  }

  fs.symlinkSync(src, dist);

  return;
}

const sourcePath = path.join(__dirname, "./core/build/@bfsx/core/dist/default/esm");
const targetPath = path.join(__dirname, "../android/app/src/main/assets/bfs");

createSymlink(sourcePath, targetPath);
