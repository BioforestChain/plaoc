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
  option.append = option.append ? false : option.append  // 是否追加内容,默认是false
  option.autoCreate = option.autoCreate ? true : option.autoCreate // 自动创建不存在的目录，默认是 true

  const fs = await network.asyncCallDenoFunction(vfsHandle.FileSystemLs, { path, option })
  return fs
}
