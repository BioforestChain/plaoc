import { list, ls } from "./src/ls.ts";
import { mkdir } from "./src/mkdir.ts";
import { read, readBuff } from "./src/read.ts";
import { write } from "./src/write.ts";
import { rm } from "./src/delete.ts";

const fs = {
  ls,
  list,
  mkdir,
  read,
  readBuff,
  write,
  rm,
};
export { fs, list, ls, mkdir, read, readBuff, rm, write };


export { EFilterType } from "./src/vfsType.ts"
