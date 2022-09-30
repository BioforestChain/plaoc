const fs = require('fs');
const path = require("path");

/*
 * 复制目录、子目录，及其中的文件
 * @param src {String} 要复制的目录
 * @param dist {String} 复制到目标目录
 */
function copyDir(src, dist, callback) {
  fs.access(dist, function (err) {
    if (err) {
      // 目录不存在时创建目录
      fs.mkdirSync(dist);
    }
    _copy(null, src, dist);
  });

  function _copy(err, src, dist) {
    if (err) {
      callback(err);
    } else {
      fs.readdir(src, function (err, paths) {
        if (err) {
          callback(err)
        } else {
          paths.forEach(function (path) {
            var _src = src + '/' + path;
            var _dist = dist + '/' + path;
            fs.stat(_src, function (err, stat) {
              if (err) {
                callback(err);
              } else {
                // 判断是文件还是目录
                if (stat.isFile()) {
                  fs.writeFileSync(_dist, fs.readFileSync(_src));
                } else if (stat.isDirectory()) {
                  // 当是目录是，递归复制
                  copyDir(_src, _dist, callback)
                }
              }
            })
          })
        }
      })
    }
  }
}

// 递归创建目录 同步方法
function mkdirsSync(dirname) {
  if (fs.existsSync(dirname)) {
    return true;
  } else {
    if (mkdirsSync(path.dirname(dirname))) {
      fs.mkdirSync(dirname);
      return true;
    }
  }
}

// // 创建目录
// mkdirsSync('../../android/app/src/main/assets/system-app/bmr9vohvtvbvwrs3p4bwgzsmolhtphsvvj');
// // 复制前端
// copyDir('./test/vue3/build', '../../android/app/src/main/assets/system-app/bmr9vohvtvbvwrs3p4bwgzsmolhtphsvvj', (e) => {
//   console.log(e)
// })
// // 复制后端
// copyDir('./test/vue3/bfs-service/dist/esm', '../../android/app/src/main/assets/system-app/bmr9vohvtvbvwrs3p4bwgzsmolhtphsvvj', (e) => {
//   console.log(e)
// })


// // 创建读取流
// readable = fs.createReadStream("./test/vue3/node_modules/@bfsx/plugin/dist/esm/common/serverWorker.mjs");

// // 创建写入流
// writable = fs.createWriteStream("../../android/app/src/main/assets/system-app/bmr9vohvtvbvwrs3p4bwgzsmolhtphsvvj/serverWorker.mjs");
// // 通过管道来传输流
// readable.pipe(writable);

/*
 * 复制目录、子目录，及其中的文件
 * @param src {String} 要复制的目录
 * @param dist {String} 复制到目标目录
 */
// function createSymlink(src, dist) {
//   if (fs.existsSync(dist)) {
//     try {
//       fs.unlinkSync(dist);
//     } catch (e) {
//       console.log(e);

//       let options = { force: true, recursive: true };
//       if (dist.endsWith(".mjs")) {
//         options = { force: true };
//       }

//       fs.rmSync(dist, options);
//     }
//   } else {
//     fs.mkdirSync(path.join(dist, "../"), { recursive: true });
//   }

//   fs.writeFileSync(src, dist);
//   console.log("创建成功");
//   return;
// }

// // 创建test-vue3链接到android项目
// const sourcePath = path.join(__dirname, "./build");
// const targetPath = path.join(__dirname, "../android/app/src/main/assets/system-app/bmr9vohvtvbvwrs3p4bwgzsmolhtphsvvj");

// createSymlink(sourcePath, targetPath);


// // 创建serverWorker链接到android项目
// const workerSrcPath = path.join(__dirname, "./node_modules/@bfsx/plugin/dist/default/esm/common/serverWorker.mjs");
// const workerDistPath = path.join(__dirname, "../android/app/src/main/assets/system-app/bmr9vohvtvbvwrs3p4bwgzsmolhtphsvvj/serverWorker.mjs");

// createSymlink(workerSrcPath, workerDistPath);

// // 创建bfs-service链接到android项目
// const plaocSrcPath = path.join(__dirname, "./bfs-service/dist/esm/plaoc");
// const plaocDistPath = path.join(__dirname, "../android/app/src/main/assets/system-app/bmr9vohvtvbvwrs3p4bwgzsmolhtphsvvj/plaoc");

// createSymlink(plaocSrcPath, plaocDistPath);

// const vueSrcPath = path.join(__dirname, "./bfs-service/dist/esm/test-vue3");
// const vueDistPath = path.join(__dirname, "../android/app/src/main/assets/system-app/bmr9vohvtvbvwrs3p4bwgzsmolhtphsvvj/test-vue3");

// createSymlink(vueSrcPath, vueDistPath);

