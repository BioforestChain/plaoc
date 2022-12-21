import tar from "tar";
import { path } from "path";

/**
 * 压缩目录为bfsa后缀
 * @param dest     压缩文件目录
 * @param bfsAppId 应用id
 */
export async function compressToSuffixesBfsa(dest: string, bfsAppId: string) {
  const cwd = path.resolve(dest, "../");

  await tar.c(
    {
      // gzip设为false，否则压缩完会多一层文件，无法访问内层目录(是压缩软件打开问题，解压完没有问题)
      // gzip: false,
      gzip: true,
      preservePaths: false,
      // 设置工作目录，不设置的话，会在压缩包中带上绝对路径
      cwd: cwd,
      // 设置压缩文件名，设置后会使用promise方式，否则是流的方式
      file: `${bfsAppId}.bfsa`,
    },
    [`${bfsAppId}`]
  );
}

/**
 * 解压
 * @param file 压缩包名
 * @param dest 目标地址
 */
export async function uncompressBfsa(file: string, dest: string) {
  await tar.x({
    file: file,
    cwd: dest,
  });
}
