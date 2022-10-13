import { ls, EFilterType, fs } from "@bfsx/vfs"

const lsFileList = await ls("/", {
  filter: [
    {
      type: EFilterType.file,
      name: ["*.ts", "index"]
    },
    {
      type: EFilterType.directroy,
      name: ["core"]
    }],
  recursive: true
})
console.log("vfs 哈哈哈: ", lsFileList)

try {
  for await (const entry of fs.list("./")) { // 也可以用异步迭代器来访问，避免列表过大
    console.log("FileSystem entry: ", entry.name, entry.extname, entry.basename,
      entry.path, entry.relativePath, entry.type)
  }
} catch (e) {
  console.log(e)
}
