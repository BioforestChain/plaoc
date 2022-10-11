// const fs = await fs.rm("/", {
//   deepDelete: true, // 是否删除包含子目录 true
// });

import { network } from "@bfsx/core";
import { vfsHandle } from "../vfsHandle.ts";
import { RmOption } from "./vfsType.ts";

export async function rm(option: RmOption) {
  const fs = await network.asyncCallDenoFunction(vfsHandle.FileSystemRm, { option })
  console.log("FileSystemRm: ", fs)
  return fs
}
