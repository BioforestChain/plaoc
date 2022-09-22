// const list: string[] = await fs.ls("./", { // list
//   filter: [{ // 声明筛选方式
//     type: "file",
//     name: ["*.ts"]
//   }],
//   recursive: true, // 是否要递归遍历目录，默认是 false
// });
import { } from "@bfsx/core"
import { IsOption } from './lsType';
export function ls(path: string, option: IsOption) {

}



// for await (const entry of fs.list("./")) { // 也可以用异步迭代器来访问，避免列表过大
//   entry.name // 文件或者目录的完整名称
//   entry.extname // 文件的后缀，如果是文件夹则为空
//   entry.basename // 文件的基本名称
//   entry.path // 完整路径
//   entry.cwd // 访问者的源路径
//   entry.relativePath // 相对路径
//   entry.type // "file"或者"directory"
//   entry.isLink // 是否是超链接文件
//   await entry.text() // {string} 当作文本读取
//   await entry.binary() // {ArrayBuffer} 当作二进制读取
//   entry.stream({ threshold...}) // {AsyncGenerator<ArrayBuffer>} 以二进制流的方式进行读取
//   await entry.readAs("json") // {json-instance} 解析成json实例对象。这是开发者可以通过扩展来实现的
//   await entry.checkname("new-name") // {boolean} 检查名字是否符合规范。在一些特定的文件夹中，通过“文件夹守护配置GuardConfig”，可能会有特定的文件名规范
//   await entry.rename("new-name") // {self} 重命名，如果名字不符合规范，会抛出异常
//   await entry.cd("../") // {FileSystem} change-directory 进入其它目录
//   await entry.open("/") // 与FileSystem.open类似，使用绝对路径打开，同时会继承第二参数的部分配置
//   entry.relativeTo("./" | otherEntry) // {string} 获取相对路径
// }
export function list() {

}
