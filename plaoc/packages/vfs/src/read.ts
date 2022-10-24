import { network } from "@bfsx/core"
import { vfsHandle } from "../vfsHandle.ts";

// const fs = await fs.read("/text.text");

/**
 * 读取文件
 * @param filePath 要读取的文件路径 
 * @returns fs
 */
export async function read(path: string) {
  const fs = await network.asyncCallDenoFunction(vfsHandle.FileSystemRead, { path })
  return fs
}

/**
 * 读取文件buffer
 * @param path 
 * @returns 
 */
export async function readBuff(path: string): Promise<ArrayBuffer> {
  const fs = await network.asyncCallDenoBuffer(vfsHandle.FileSystemReadBuffer, { path })
  return fs
}
