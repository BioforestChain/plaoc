
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

var fs = require('fs');

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

copyDir('./core/build/@bfsa/core/dist/default/esm', "../app/src/main/assets/bfs", function (err) {
  if (err) {
    console.log(err);
  }
})
