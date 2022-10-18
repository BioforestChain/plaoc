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
console.log("vfs测试：获取ls : ", lsFileList)

try {
  for await (const entry of fs.list("./")) { // 也可以用异步迭代器来访问，避免列表过大
    console.log(`vfs测试：获取${entry.type}的各项信息: `, entry.name, entry.extname, entry.basename,
      entry.path, entry.relativePath)
  }
} catch (e) {
  console.log(e)
}




const mkdirFs1 = await fs.mkdir("/water1")
const mkdirFs2 = await fs.mkdir("/bang1")
console.log("vfs测试：创建文件: ", mkdirFs1, mkdirFs2)

const rmDir = Math.random() <= 0.5 ? "/water1" : "/bang1"
const rmFs1 = await fs.rm(rmDir)
console.log(`vfs测试：删除${rmDir}:${rmFs1}`)


const mkdirFs3 = await fs.mkdir("/water/bang", { recursive: true })
console.log("vfs测试：创建多级文件: ", mkdirFs3);

const rmFs2 = await fs.rm("/water", { deepDelete: false })
const rmFs3 = await fs.rm("/water")
console.log(`vfs测试：递归删除失败 ${rmFs2}`)
console.log(`vfs测试：递归删除 ${rmFs3}`)


