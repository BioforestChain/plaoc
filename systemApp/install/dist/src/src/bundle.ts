import * as fs from "fs";
import * as process from "process";
import { Files, LinkMetadata, MetaData } from "@bfsx/metadata";
import { path } from "./path.js";
import { checksumFile } from "./crypto.js";
import { compress } from "../deps/deno.land/x/compress@v0.4.5/tar/mod.js";

import "@bfsx/typings";

const { existsSync } = fs;
const { mkdir, writeFile, copyFile, readdir, stat } = fs.promises;

const bfsAppId = "AK12LK23";

/**
 * 打包入口
 * @param options
 */
export async function bundle(options: {
  bfsAppId: string;
  frontPath: string;
  backPath: string;
}) {
  // const destPath = await createBfsaDir(options.bfsAppId);
  const { frontPath, backPath } = options;
  const destPath = await createBfsaDir(bfsAppId);

  // 将前端项目移动到sys目录
  const sysPath = path.join(destPath, "sys");
  await copyDir(frontPath, sysPath);
  await writeServiceWorkder(sysPath);

  // 将后端项目移动到boot目录
  const bootPath = path.join(destPath, "boot");
  await copyDir(backPath, bootPath);
  const metadata = await getBfsaMetaDataJson(bootPath);
  await writeConfigJson(bootPath, bfsAppId, metadata);

  compress(destPath, "./");
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
      await copyDir(srcPath, destPath);
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
  const content =
    'const t=self,n=[];t.addEventListener("install",(function(n){n.waitUntil(t.skipWaiting())})),t.addEventListener("activate",(function(n){n.waitUntil(t.clients.claim())})),t.addEventListener("fetch",(async function(t){const e=t.request;e.method.match(/POST/i)?function(t){t.respondWith(async function(){await async function(t,e){if(null===t.body)return;const a=await t.arrayBuffer(),s=function(t){let n=0,e=524288;const a=[];do{a.push(t.subarray(n,n+e)),n+=e}while(t.byteLength>n);return a}(new Uint8Array(a)),i=[];await Promise.all(s.map((async t=>{const n=await async function(t,n){const e=t.request;return await fetch(`${e.url}?upload=${n}`,{headers:e.headers,method:"GET",mode:"cors"})}(e,t);i.push(await n.text())}))),n.push(new Response(String(i)))}(t.request,t);const e={next:async()=>{const t=n.shift();return t?{value:t,done:!1}:{value:new Response,done:!0}}},{value:a,done:s}=await e.next();return a}())}(t):t.respondWith(async function(){return await fetch(e)}())})),self.export="";';

  await writeFile(file, content, "utf-8");

  return true;
}

/**
 * 获取bfsa-metadata.json文件的数据
 * @param bootPath boot目录
 * @returns
 */
async function getBfsaMetaDataJson(bootPath: string): Promise<MetaData> {
  const bfsaMetaDataFile = await searchFile(bootPath, /bfs-metadata.m?js/);

  if (!bfsaMetaDataFile) {
    throw new Error("未找到bfsa-metadata配置文件");
  }

  const metadata = await import(bfsaMetaDataFile);

  return metadata;
}

/**
 * 复制icon到boot目录
 * @param bootPath boot目录
 * @param iconName icon名
 * @returns
 */
async function copyIcon(bootPath: string, iconName: string) {
  const reg = new RegExp(iconName);
  const iconPath = await searchFile(path.resolve(bootPath, "../"), reg);

  if (!iconPath) {
    throw new Error("未找到应用icon");
  }

  await copyFile(iconPath, bootPath);

  return;
}

/**
 * 在boot目录写入bfs-metadata.json和link.json，复制icon
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
    path.join(bootPath, "bfs-metadata.json"),
    JSON.stringify(metadata),
    "utf-8"
  );

  // 复制icon到boot目录
  const { manifest } = metadata;
  const iconName = path.basename(manifest.icon);
  await copyIcon(bootPath, iconName);

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

// function genBfsaMetaDataJson(
//   bfsAppId: string,
//   metadata: MetaData
// ): Promise<string> {

// }

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

  const iconName = path.basename(manifest.icon);
  const linkJson: LinkMetadata = {
    version: manifest.version,
    bfsAppId: bfsAppId,
    name: manifest.name,
    icon: `file:///boot/${iconName}`,
    author: manifest.author || [],
    autoUpdate: {
      // maxAge: manifest.maxAge || 6 * 60,
      maxAge: 6 * 60,
      provider: 1,
      url: `https://shop.plaoc.com/${bfsAppId}.bfsa`,
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
 * 搜索文件获取地址
 * @param src     搜索文件路径
 * @param nameReg 搜索文件正则
 * @returns
 */
async function searchFile(
  src: string,
  nameReg: RegExp
): Promise<string | null> {
  const entries = await readdir(src, { withFileTypes: true });

  for (const entry of entries) {
    const filePath = path.join(src, entry.name!);
    if (nameReg.test(entry.name!) && entry.isFile()) {
      return filePath;
    } else if (entry.isDirectory()) {
      await searchFile(filePath, nameReg);
    }
  }

  return null;
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
        url: `https://shop.plaoc.com/${bfsAppId}${filePath.slice(
          filePath.lastIndexOf(bfsAppId) + bfsAppId.length
        )}`,
        size: fileStat.size,
        sha512: fileHash,
      };

      filesList.push(file);
    } else {
      await fileListHash(dest, bfsAppId, filesList);
    }
  }

  return filesList;
}
