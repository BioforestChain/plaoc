import * as fs from "node_fs";
import * as process from "node_process";
import { fileURLToPath, pathToFileURL, URL } from "node_url";
import { Files, LinkMetadata, MetaData } from "@bfsx/metadata";
import { path, slash } from "path";
import { checksumFile } from "crypto";
import { genBfsAppId, checkSign } from "check";
import { compressToSuffixesBfsa } from "compress";

import type * as types from "@bfsx/typings";

const { existsSync } = fs;
const { mkdir, writeFile, copyFile, readdir, stat, rm, readFile } = fs.promises;

/**
 * 打包入口
 * @param options
 */
export async function bundle(options: {
  bfsAppId: string;
  frontPath?: string;
  backPath: string;
}) {
  let bfsAppId = options.bfsAppId;

  // 校验bfsAppId是否合法
  if (bfsAppId) {
    const suc = await checkSign(bfsAppId);

    if (!suc) {
      throw new Error("bfsAppId不合法，请输入正确的bfsAppId");
    }
  } else {
    bfsAppId = await genBfsAppId();
  }

  const { frontPath, backPath } = options;
  const destPath = await createBfsaDir(bfsAppId);

  // 将前端项目移动到sys目录
  // 无界面应用不包含前端
  if (frontPath) {
    const sysPath = path.join(destPath, "sys");
    await copyDir(frontPath, sysPath);
    await writeServiceWorkder(sysPath);
  }

  // 将后端项目移动到boot目录
  const bootPath = path.join(destPath, "boot");
  // copy时没有带上目录，因此加上项目目录
  const backname = path.basename(backPath);
  await copyDir(backPath, path.join(bootPath, backname));
  const metadata = await getBfsaMetaDataJson(bootPath, backPath);
  await writeConfigJson(bootPath, bfsAppId, metadata);

  // 对文件进行压缩
  await compressToSuffixesBfsa(destPath, bfsAppId);

  // 压缩完成，删除目录
  await rm(destPath, { recursive: true });

  // 生成appversion.json
  await genAppVersionJson(bfsAppId, metadata, destPath);

  console.log("bundle bfsa application done!!!");
}

/**
 * 创建打包目录
 * @param bfsAppId 应用appid，未来该数据需要从链上申请，所以格式需要保持一致：
 *                 长度为7+1（校验位）的大写英文字母或数字（链就是系统的“证书颁发机构”，
 *                 资深用户可以配置不同的的链来安装那些未知来源的应用）
 * @returns {boolean}
 */
async function createBfsaDir(bfsAppId: string): Promise<string> {
  const root = process.cwd();

  try {
    const destPath = path.join(root, bfsAppId);

    if (existsSync(destPath)) {
      await rm(destPath, { recursive: true });
    }

    // 创建bfsAppId目录
    await mkdir(destPath, { recursive: true });
    await mkdir(path.join(destPath, "boot"));
    await mkdir(path.join(destPath, "sys"));
    await mkdir(path.join(destPath, "tmp"));
    await mkdir(path.join(destPath, "home"));
    await mkdir(path.join(destPath, "usr"));

    return destPath;
  } catch (ex) {
    throw Error(ex.message);
  }
}

/**
 * 复制目录
 * @param src  源目录
 * @param dest 目标目录
 */
async function copyDir(src: string, dest: string) {
  const entries = await readdir(src, { withFileTypes: true });

  if (!existsSync(dest)) {
    await mkdir(dest);
  }

  for (const entry of entries) {
    const srcPath = path.join(src, entry.name!);
    const destPath = path.join(dest, entry.name!);

    if (entry.isDirectory()) {
      // 排除node_modules
      if (entry.name !== "node_modules") {
        await copyDir(srcPath, destPath);
      }
    } else {
      await copyFile(srcPath, destPath);
    }
  }
}

/**
 * 将serviceWorker写入到项目中
 * @param destPath 目标目录
 * @returns
 */
async function writeServiceWorkder(destPath: string): Promise<boolean> {
  const file = path.join(destPath, "serverWorker.mjs");

  // TODO: 暂时没想到好的方法
  const url = new URL("./bundle.js", import.meta.url);
  const filePath = path.dirname(fileURLToPath(url.href));
  let content = await readFile(
    path.resolve(
      filePath,
      "../../node_modules/@bfsx/gateway/esm/serverWorker.js"
    ),
    "utf-8"
  );
  content = content.replace(/export/g, "");
  await writeFile(file, content, "utf-8");

  return true;
}

/**
 * 获取bfsa-metadata.json文件的数据
 * @param bootPath boot目录
 * @returns
 */
async function getBfsaMetaDataJson(
  bootPath: string,
  backPath: string
): Promise<MetaData> {
  const packageJsonPath = await searchFile(bootPath, /package.json/);
  const bfsaMetaDataFile = await searchFile(backPath, /bfsa-metadata\.m?js/);

  if (!bfsaMetaDataFile) {
    throw new Error("未找到bfsa-metadata配置文件");
  }

  const url = pathToFileURL(bfsaMetaDataFile);
  const metadata = await import(url.href);

  const jsonPath = pathToFileURL(packageJsonPath);
  const jsonConfig = (await import(jsonPath.href, { assert: { type: "json" } }))
    .default;
  const backDir = path.dirname(packageJsonPath);
  const rootPath = path.resolve(bootPath, "../");
  let bfsaEntry = "";

  if (jsonConfig.main) {
    bfsaEntry = path.relative(rootPath, path.resolve(backDir, jsonConfig.main));
  } else if (jsonConfig.module) {
    bfsaEntry = path.relative(
      rootPath,
      path.resolve(backDir, jsonConfig.module)
    );
  } else if (jsonConfig.exports?.["."]?.import) {
    const entry =
      typeof jsonConfig.exports["."].import === "string"
        ? jsonConfig.exports["."].import
        : jsonConfig.exports["."].import.default;
    bfsaEntry = path.relative(rootPath, path.resolve(backDir, entry));
  }

  const _metadata: MetaData = {
    manifest: {
      ...metadata.default.manifest,
      bfsaEntry: bfsaEntry ? "./" + slash(bfsaEntry) : "",
    },
    dwebview: metadata.default.dwebview,
    whitelist: metadata.default.whitelist,
  };

  return _metadata;
}

/**
 * 在boot目录写入bfs-metadata.json和link.json
 * @param bootPath boot目录
 * @param bfsAppId 应用id
 * @param metadata bfs-metadata数据
 * @returns
 */
async function writeConfigJson(
  bootPath: string,
  bfsAppId: string,
  metadata: MetaData
) {
  // bfsa-metadata.json
  await writeFile(
    path.join(bootPath, "bfsa-metadata.json"),
    JSON.stringify(metadata),
    "utf-8"
  );

  // 文件列表生成校验码
  const destPath = path.resolve(bootPath, "../");
  const filesList: Files[] = await fileListHash(
    destPath,
    bfsAppId,
    [] as Files[]
  );

  // link.json
  const linkJson = await genLinkJson(bfsAppId, metadata, filesList);
  await writeFile(
    path.join(bootPath, "link.json"),
    JSON.stringify(linkJson),
    "utf-8"
  );

  return;
}

/**
 * 生成link.json
 * @param bfsAppId  应用id
 * @param metadata  bfs-metadata数据
 * @param filesList 文件列表
 * @returns
 */
function genLinkJson(
  bfsAppId: string,
  metadata: MetaData,
  filesList: Files[]
): LinkMetadata {
  const { manifest } = metadata;

  // 最大缓存时间，一般6小时更新一次。最快不能快于1分钟，否则按1分钟算。
  const maxAge = manifest.maxAge
    ? manifest.maxAge < 1
      ? 1
      : manifest.maxAge
    : 6 * 60;

  const linkJson: LinkMetadata = {
    version: manifest.version,
    bfsAppId: bfsAppId,
    name: manifest.name,
    icon: manifest.icon,
    author: manifest.author || [],
    autoUpdate: {
      maxAge: maxAge,
      provider: 1,
      url: `https://shop.plaoc.com/${bfsAppId}/appversion.json`,
      version: manifest.version,
      files: filesList,
      releaseNotes: manifest.releaseNotes || "",
      releaseName: manifest.releaseName || "",
      releaseDate: manifest.releaseDate || "",
    },
  };

  return linkJson;
}

/**
 * 生成appversion.json
 * @param bfsAppId 应用id
 * @param metadata 应用配置信息
 * @param destPath 应用目录
 * @returns
 */
async function genAppVersionJson(
  bfsAppId: string,
  metadata: MetaData,
  destPath: string
) {
  const { manifest } = metadata;
  const compressFile = path.resolve(destPath, `../${bfsAppId}.bfsa`);
  const fileStat = await stat(compressFile);
  const fileHash = await checksumFile(compressFile, "sha512", "hex");

  const appVersionJson = (
    await import("../assets/appversion.json", { assert: { type: "json" } })
  ).default;

  appVersionJson.data = {
    version: manifest.version,
    name: manifest.name,
    icon: manifest.icon,
    files: [
      {
        url: `https://shop.plaoc.com/${bfsAppId}/${bfsAppId}.bfsa`,
        size: fileStat.size,
        sha512: fileHash,
      },
    ],
    releaseNotes: manifest.releaseNotes || "",
    releaseName: manifest.releaseName || "",
    releaseDate: manifest.releaseDate || "",
  };

  await writeFile(
    path.resolve(destPath, "../appversion.json"),
    JSON.stringify(appVersionJson, null, 2),
    "utf-8"
  );

  return;
}

/**
 * 搜索文件获取地址
 * @param src     搜索文件路径
 * @param nameReg 搜索文件正则
 * @returns
 */

async function searchFile(src: string, nameReg: RegExp): Promise<string> {
  let searchPath = "";
  await loopSearchFile(src, nameReg);

  async function loopSearchFile(src: string, nameReg: RegExp) {
    if (!searchPath) {
      const entries = await readdir(src, { withFileTypes: true });

      for (const entry of entries) {
        const filePath = path.join(src, entry.name!);
        if (nameReg.test(entry.name!) && entry.isFile()) {
          searchPath = filePath;
          break;
        } else if (entry.isDirectory()) {
          await loopSearchFile(filePath, nameReg);
        }
      }
    }

    return;
  }

  return searchPath;
}

/**
 * 为文件列表生成sha512校验码
 * @param dest        查找目录
 * @param bfsAppId    应用id
 * @param filesList   文件列表hash
 * @returns
 */
async function fileListHash(
  dest: string,
  bfsAppId: string,
  filesList: Files[]
): Promise<Files[]> {
  const entries = await readdir(dest, { withFileTypes: true });

  for (const entry of entries) {
    const filePath = path.join(dest, entry.name!);

    if (entry.isFile()) {
      const fileStat = await stat(filePath);
      const fileHash = await checksumFile(filePath, "sha512", "hex");
      const file: Files = {
        url: `https://shop.plaoc.com/${bfsAppId}${slash(
          filePath.slice(filePath.lastIndexOf(bfsAppId) + bfsAppId.length)
        )}`,
        size: fileStat.size,
        sha512: fileHash,
      };

      filesList.push(file);
    } else if (entry.isDirectory()) {
      await fileListHash(filePath, bfsAppId, filesList);
    }
  }

  return filesList;
}
