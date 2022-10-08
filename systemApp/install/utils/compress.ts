import tar from "tar";
import { path } from "path";

export async function compressToSuffixesBfsa(dest: string, bfsAppId: string) {
  const cwd = path.resolve(dest, "../");

  await tar.c(
    {
      // gzip设为false，否则压缩完会多一层文件，无法访问内层目录
      gzip: false,
      preservePaths: false,
      // 设置工作目录，不设置的话，会在压缩包中带上绝对路径
      cwd: cwd,
      // 设置压缩文件名，设置后会使用promise方式，否则是流的方式
      file: `${bfsAppId}.bfsa`,
    },
    [`${bfsAppId}`]
  );
}
