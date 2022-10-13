import {
  copySync,
  moveSync,
  move,
  walkSync,
} from "https://deno.land/std@0.156.0/fs/mod.ts";
import {
  build,
  emptyDir,
  EntryPoint,
  LibName,
} from "https://deno.land/x/gaubee_dnt@0.31.0-1/mod.ts";

export const doBuid = async (config: {
  name: string;
  version: string;
  mainExports: string;
  buildFromRootDir: string;
  buildToRootDir: string;
  importMap?: string;
  lib?: (LibName | string)[];
}) => {
  const {
    version,
    mainExports,
    buildFromRootDir,
    buildToRootDir,
    importMap,
    name,
    lib,
  } = config;
  console.log(`--- START BUILD: ${name} ${version} ---`);
  const pkgFilter = new Map<string, { entryPointName?: string }>([
    [
      mainExports,
      {
        entryPointName: ".",
      },
    ],
  ]);

  await emptyDir(buildToRootDir);

  const entryPoints: EntryPoint[] = [];

  for await (const dirEntry of Deno.readDir(buildFromRootDir)) {
    if (dirEntry.isDirectory === false) {
      continue;
    }

    const config = pkgFilter.get(dirEntry.name) || {};

    // console.group("entry-point:", dirEntry.name, config);

    const packageBaseName = dirEntry.name
      .replace(/[_]/g, "-")
      .replace(/[A-Z]/g, (c) => "-" + c.toLowerCase())
      .replace(/^[\-]+/, "")
      .replace(/[\-]+/g, "-");

    const buildFromDir = `${buildFromRootDir}/${dirEntry.name}`;
    // const buildToDir = `${BUILD_TO_ROOT_DIR}/${dirEntry.name}`;

    entryPoints.push({
      name: config.entryPointName || "./" + packageBaseName,
      path: `${buildFromDir}/index.ts`,
    });

    // console.groupEnd();
  }

  const orderMap = new Map([[".", 100]]);
  {
    const getOrder = (ep: EntryPoint) => orderMap.get(ep.name) || 1;
    entryPoints.sort((a, b) => getOrder(b) - getOrder(a));
  }

  await build({
    importMap: importMap,
    entryPoints: entryPoints,
    outDir: buildToRootDir,
    /**
     * @TODO should ignore errors:
     * 1. TS2691
     */
    typeCheck: true,
    shims: {
      // see JS docs for overview and more options
      deno: "dev",
    },
    compilerOptions: {
      target: "ES2020",
      importHelpers: true,
      isolatedModules: false,
      experimentalDecorators: true,
      emitDecoratorMetadata: true,
      lib: lib as LibName[],
    },
    packageManager: "npm",
    package: {
      // package.json properties
      name: name,
      version: version,
      description: `bnqkl util`,
      license: "MIT",
      repository: {
        type: "git",
        url: "git+https://github.com/BioforestChain/Util.git",
      },
      bugs: {
        url: "https://github.com/BioforestChain/Util/issues",
      },
    },
  });

  // merge esm/script/types
  const mergeDir = `${buildToRootDir}/.merge`;
  {
    const esmDir = `${buildToRootDir}/esm`;
    const cjsDir = `${buildToRootDir}/script`;
    const typesDir = `${buildToRootDir}/types`;
    const srcDir = `${buildToRootDir}/src`;

    /**寻找一个不冲突的文件夹名称 */
    const findUseableName = (dir: string, expect_name: string) => {
      let name = expect_name;
      let try_suffix = 1;
      while (true) {
        try {
          Deno.statSync(`${dir}/${name}`);
          name = `${expect_name}-${try_suffix++}`;
        } catch (err) {
          /// 如果找不到，那么就报错了，那就说明这个名字可用
          if (err instanceof Deno.errors.NotFound) {
            return name;
          }
          throw err;
        }
      }
    };
    const mapExportsKey = (
      obj: any,
      matchKey: string,
      mapper: (key: string) => string
    ) => {
      for (const key in obj) {
        const val = obj[key];
        if (key === matchKey) {
          if (typeof val === "object" && "default" in val) {
            val.default = mapper(val.default);
          } else if (typeof val === "string") {
            obj[key] = mapper(val);
          }
        } else if (typeof val === "object") {
          mapExportsKey(obj[key], matchKey, mapper);
        }
      }
    };
    const orderExportsKey = (obj: any) => {
      if (typeof obj === "object") {
        // types 一定要在最前面
        // default 一定要在最后面
        // 详见 https://nodejs.org/api/packages.html
        if ("default" in obj || "types" in obj) {
          const backupObj = { ...obj };
          for (const key in obj) {
            delete obj[key];
          }
          obj.types = backupObj.types;
          for (const key in backupObj) {
            obj[key] = backupObj[key];
          }
          delete obj.default;
          obj.default = backupObj.default;
        }
        for (const key in obj) {
          orderExportsKey(obj[key]);
        }
      }
    };

    /**遍历“包”文件夹 */
    const walkPackages = function* (dir: string) {
      let first = true;
      for (const entry of walkSync(dir, {
        maxDepth: 1 /* includeFiles: false */,
      })) {
        if (first) {
          /// 跳过dir
          first = false;
          continue;
        }
        // const exportsKey =
        //   entry.name === mainExports
        //     ? "."
        //     : `./${entry.name.replace(/_/g, "-")}`;
        // const exportsValue = packageExports[exportsKey];
        // console.log(entry.name, exportsKey, exportsValue);

        yield {
          entry,
          // exportsKey,
          // exportsValue,
          // mapExportsKey: (matchKey: string, mapper: (key: string) => string) =>
          //   mapExportsKey(exportsValue, matchKey, mapper),
        };
      }
    };
    /// 要更改的packjson exports
    const packageJson = JSON.parse(
      Deno.readTextFileSync(`${buildToRootDir}/package.json`)
    );
    const packageExports = packageJson.exports;

    /// types 会直接平铺到 toDir 目录下，所以我们需要确保 esm 文件夹和 scripts 文件夹与 types 内的文件夹不冲突
    /// esm
    const esmDirname = findUseableName(typesDir, "esm");
    if (esmDirname !== "esm") {
      moveSync(esmDir, `${buildToRootDir}/${esmDirname}`);
      mapExportsKey(packageExports, "import", (oldEsmPath) => {
        const newEsmPath = oldEsmPath.replace(/^\.\/esm\//, `./${esmDirname}/`);
        return newEsmPath;
      });
    }
    /// cjs
    const cjsDirname = findUseableName(typesDir, "cjs");
    {
      moveSync(cjsDir, `${buildToRootDir}/${cjsDirname}`);
      mapExportsKey(packageExports, "require", (oldCjsPath) => {
        const newCjsPath = oldCjsPath.replace(
          /^\.\/script\//,
          `./${cjsDirname}/`
        );
        return newCjsPath;
      });
    }
    /// src
    Deno.removeSync(srcDir, { recursive: true });
    /// types
    const typesTmpDirname = findUseableName(typesDir, ".types-tmp"); // 先转移到一个缓存文件夹，避免有 types/types 这样的文件夹
    const typesTmpDir = `${buildToRootDir}/${typesTmpDirname}`;
    moveSync(typesDir, typesTmpDir);
    for (const { entry } of walkPackages(typesTmpDir)) {
      /// 更改 exports.*.types 配置
      moveSync(entry.path, `${buildToRootDir}/${entry.name}`);
    }
    mapExportsKey(packageExports, "types", (oldTypePath) => {
      const newTypePath = oldTypePath.replace(/^\.\/types\//, "./");
      return newTypePath;
    });
    Deno.removeSync(typesTmpDir, {});

    /// 更改 "module" "main" "types"
    mapExportsKey(packageJson.exports["."], "import", (path) => {
      return (packageJson.module = path);
    });
    mapExportsKey(packageJson.exports["."], "require", (path) => {
      return (packageJson.main = path);
    });
    mapExportsKey(packageJson.exports["."], "types", (path) => {
      return (packageJson.types = path);
    });

    /// 对 package.json 的 exports 的字段进行重排序，确保符合规范
    orderExportsKey(packageExports);
    /// 写入 package.json
    Deno.writeTextFileSync(
      `${buildToRootDir}/package.json`,
      JSON.stringify(packageJson, null, 2)
    );
  }

  // post build steps
  for (const base of ["README.md", "LICENSE"]) {
    const fromFilename = `${buildFromRootDir}/${base}`;
    const toFilename = `${buildToRootDir}/${base}`;
    try {
      copySync(fromFilename, toFilename, { overwrite: true });
    } catch (err) {
      if (err instanceof Deno.errors.NotFound === false) {
        throw err;
      }
    }
  }
};

import * as semver from "https://deno.land/std@0.156.0/semver/mod.ts";

export const getVersionGenerator = (version_input?: string) => {
  let getVersion = (version: string) => {
    return version;
  };
  if (version_input) {
    if (version_input.startsWith("+")) {
      const [release, identifier] = version_input
        .slice(1)
        .split(/\:/)
        .map((v, index) => {
          if (index === 0) {
            switch (v) {
              case "1":
                return "patch";
              case "1.0":
                return "minor";
              case "1.0.0":
                return "major";
              case "pre":
                return "prerelease";
            }
          }
          return v;
        });
      if (
        !(
          release === "major" ||
          release === "minor" ||
          release === "patch" ||
          (release === "prerelease" && typeof identifier === "string")
        )
      ) {
        console.error(
          "请输入正确的 ReleaseType: major, minor, patch, prerelease:identifier"
        );
        Deno.exit(0);
      }
      // major, minor, patch, or prerelease
      getVersion = (version) =>
        semver.inc(version, release, undefined, identifier) || version;
    } else {
      const semver_version = semver.minVersion(version_input);
      if (semver_version === null) {
        console.error("请输入正确的待发布版本号");
        Deno.exit(0);
      }

      getVersion = () => semver_version.toString();
    }
  }
  return getVersion;
};

export const doBuildFromJson = async (file: string, args = Deno.args) => {
  const getVersion = getVersionGenerator(args[0]);
  const npmConfigs = (await import(file, { assert: { type: "json" } })).default;

  for (const config of npmConfigs) {
    await doBuid({
      ...config,
      version: getVersion(config.version),
    });
  }
};

if (import.meta.main) {
  // deno-lint-ignore no-explicit-any
  await doBuildFromJson((import.meta as any).resolve("./npm.json"));
}
