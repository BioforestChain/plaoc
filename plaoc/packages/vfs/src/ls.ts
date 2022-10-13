import { network } from "@bfsx/core"
import { IsOption } from "./vfsType.ts";
import { vfsHandle } from '../vfsHandle.ts';
import { FileEntry } from './vfsType.ts';
import { EFilterType } from './vfsType.ts';

/// const list: string[] = await fs.ls("./", { // list
///   filter: [{ // 声明筛选方式
///     type: "file",
///     name: ["*.ts"]
///   }],
///   recursive: true, // 是否要递归遍历目录，默认是 false
/// });

/**
 * 获取目录下有哪些文件
 * @param path 
 * @param option:{filter: [{type: "file", name: ["*.ts"]}],recursive: true // 是否要递归遍历目录，默认是 false}
 * @returns file string[]
 */
export async function ls(path: string, option: IsOption) {
  const fileList = await network.asyncCallDenoFunction(vfsHandle.FileSystemLs, { path, option })
  return transStringToArray(fileList);
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

/**
 * 返回文件对象
 * @param path 
 * @returns fileSystems
 */
export async function* list(path: string): AsyncGenerator<FileEntry> {
  const fileList = await network.asyncCallDenoFunction(vfsHandle.FileSystemList, { path })
  const list = transStringToArray(fileList);
  for (const filePath of list) {
    const files = createFileEntry(filePath, path)
    yield files
  }
}


// ["/src/test/vue3/bfsa-service/vfs/index.ts","./src"]
/**
 * 创建文件entry
 * @param filePath 
 * @param cwd 
 * @returns 
 */
function createFileEntry(filePath: string, cwd: string) {
  // 去掉两边的"
  filePath = filePath.replace(/^"/, "").replace(/"$/, "");
  const file: FileEntry = {} as FileEntry
  const isFile = /.+\..+/.test(filePath);

  file.name = filePath.slice(filePath.lastIndexOf("/") + 1);
  file.path = filePath;
  file.cwd = cwd;
  file.relativePath = filePath.slice(0, filePath.lastIndexOf("/") + 1);
  file.type = isFile ? EFilterType.file : EFilterType.directroy;
  file.basename = isFile
    ? file.name.slice(0, file.name.lastIndexOf(".") + 1)
    : file.name;
  file.isLink = false;
  file.extname = "";
  if (isFile) {
    file.extname = filePath.slice(filePath.lastIndexOf("."));
  }

  return file
}

/**
 * 把字符串转换成数组
 * @param string
 * @returns string[]
 */
function transStringToArray(str: string) {
  if (/^\[.*\]$/i.test(str)) {
    str = str.replace(/^\[/i, "").replace(/\]$/i, "");
  }
  return str.split(",");
}
