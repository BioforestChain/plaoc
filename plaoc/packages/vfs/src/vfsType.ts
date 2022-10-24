export interface IsOption {
  filter: IsFilter[]; //声明筛选方式
  recursive: boolean; // 是否要递归遍历目录，默认是 false
}

/** * 声明筛选方式*/
export interface IsFilter {
  type: EFilterType;
  name: string[];
}

export enum EFilterType {
  file = "file",
  directory = "directory",
}


export interface MkdirOption {
  recursive: boolean
}

export interface WriteOption {
  content: string,
  append?: boolean,
  autoCreate?: boolean
}

export interface RmOption {
  deepDelete: boolean
}

export interface FileEntry {
  name: string, // 文件或者目录的完整名称
  extname: string, // 文件的后缀，如果是文件夹则为空
  basename: string, // 文件的基本名称
  path: string, // 完整路径
  cwd: string, // 访问者的源路径
  relativePath: string, // 相对路径
  type: EFilterType, // "file"或者"directory"
  isLink: boolean, // 是否是超链接文件
  text: textFn, // {string} 当作文本读取
  binary: binaryFn, // {ArrayBuffer} 当作二进制读取
  // readAs: readAsFn, // {json-instance} 解析成json实例对象。这是开发者可以通过扩展来实现的
  stream: streamFn, // {AsyncGenerator<ArrayBuffer>} 以二进制流的方式进行读取
  // checkname: checknameFn, // {boolean} 检查名字是否符合规范。在一些特定的文件夹中，通过“文件夹守护配置GuardConfig”，可能会有特定的文件名规范
  rename: renameFn,  // {self} 重命名，如果名字不符合规范，会抛出异常
  cd: cdFn, // {FileSystem} change-directory 进入其它目录
  // open: openFn, // 与FileSystem.open类似，使用绝对路径打开，同时会继承第二参数的部分配置
  relativeTo: relativeToFn  // {string} 获取相对路径
}

type textFn = () => Promise<string>;
type binaryFn = () => Promise<ArrayBuffer | Error>;
type readAsFn = () => Promise<FileEntry>;
type streamFn = () => AsyncGenerator<ArrayBuffer | Error>;
type checknameFn = () => Promise<boolean>;
type renameFn = (name: string) => Promise<string | boolean>;
type cdFn = (path: string) => Promise<FileEntry[]>;
// type openFn = () => Promise<boolean>;
type relativeToFn = (path?: string) => Promise<string>;

