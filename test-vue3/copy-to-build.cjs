var fs = require('fs');
// 创建读取流
readable = fs.createReadStream("./node_modules/@bfsx/plugin/dist/esm/common/serverWorker.mjs");
// 创建写入流
writable = fs.createWriteStream("./serverWorker.mjs");
// 通过管道来传输流
readable.pipe(writable);
