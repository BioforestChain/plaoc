// const fs = await fs.rm("/", {
//   deepDelete: true, // 是否删除包含子目录 true
// });

// import { network } from "@bfsx/core";
import { vfsHandle } from "../vfsHandle.ts";
import { RmOption } from "./vfsType.ts";

/**
 *
 * @param path 默认值就是根目录
 * @param option : { deepDelete: true, // 是否删除包含子目录 true}
 * @returns
 */
export async function rm(
  path: string,
  option: RmOption = { deepDelete: true }
) {
  const fs = await network.asyncCallDenoFunction(vfsHandle.FileSystemRm, {
    path,
    option,
  });
  return fs;
}
