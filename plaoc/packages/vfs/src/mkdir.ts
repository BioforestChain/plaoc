
import { network } from "@bfsx/core"
import { vfsHandle } from "../vfsHandle.ts";
import { MkdirOption } from "./vfsType.ts";


// const fs = await fs.open("/"/* 默认值就是根目录 */, {
//   recursive: true, // 自动创建不存在的目录，默认是 false
// });

/**
 * 
 * @param path 默认值就是根目录
 * @param option : { recursive: false } // 自动创建不存在的目录，默认是 false
 * @returns fs
 */
export async function mkdir(path: string, option: MkdirOption = { recursive: false }) {
  const result = await network.asyncCallDenoFunction(vfsHandle.FileSystemMkdir, { path, option })
  return result
}
