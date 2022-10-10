import { list, ls } from "./src/ls.ts";
import { mkdir } from "./src/open.ts";
import { read } from "./src/read.ts";
import { write } from "./src/write.ts";
import { rm } from "./src/delete.ts";

const fs = {
  ls,
  list,
  mkdir,
  read,
  write,
  rm,
};
export { fs, list, ls, mkdir, read, rm, write };


export { EFilterType } from "./src/vfsType.ts"
