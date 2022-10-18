import { network } from "@bfsx/core"
import { vfsHandle } from "../vfsHandle.ts";
import { WriteOption } from "./vfsType.ts";


/**
 * 写入内容
 * @param filePath 
 * @param option： {content:"",append: false, // 是否追加内容,默认是false autoCreate: true, // 自动创建不存在的目录，默认是 true});
 * @returns 
 */
export async function write(path: string, option: WriteOption) {
  const fs = await network.asyncCallDenoFunction(vfsHandle.FileSystemWrite, { path, option })
  return fs
}
