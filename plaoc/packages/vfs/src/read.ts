// import { network } from "@bfsx/core";
import { vfsHandle } from "../vfsHandle.ts";
import { Path } from "@bfsx/vfs/path";

// const fs = await fs.read("/text.text");

/**
 * 读取文件
 * @param filePath 要读取的文件路径
 * @returns fs
 */
export async function read(path: string) {
  const fs = await network.asyncCallDenoFunction(vfsHandle.FileSystemRead, {
    path,
  });
  return fs;
}

/**
 * 读取文件buffer
 * @param path
 * @returns
 */
export async function readBuff(path: string): Promise<ArrayBuffer> {
  const fs = await network.asyncCallDenoBuffer(vfsHandle.FileSystemReadBuffer, {
    path,
  });
  return fs;
}

/**
 * 重命名文件
 * @param path 源文件
 * @param newPath 需要重命名的文件名
 * @returns
 */
export async function rename(
  path: string,
  newName: string
): Promise<string | boolean> {
  // 提取文件前缀 /a/b/bfsa.txt -> /a/b/
  const newPath = Path.join(path.slice(0, path.lastIndexOf("/") + 1), newName);
  const fs = await network.asyncCallDenoFunction(vfsHandle.FileSystemRename, {
    path,
    newPath,
  });
  if (fs === "true") {
    return true;
  }
  return fs;
}
