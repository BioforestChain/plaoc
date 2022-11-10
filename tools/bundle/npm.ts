import { build, emptyDir, type BuildOptions } from "dnt";
import npmConfig from "./npm.json" assert { type: "json" };

export const buildOptions: BuildOptions = {
  importMap: npmConfig.importMap,
  entryPoints: [
    {
      kind: "bin",
      name: "bfsa",
      path: "./bin/bfsa.cmd.ts",
    },
  ],
  outDir: npmConfig.outDir,
  typeCheck: true,
  scriptModule: false,
  shims: {
    deno: "dev",
  },
  compilerOptions: {
    target: "Latest",
    importHelpers: true,
    isolatedModules: false,
  },
  packageManager: "yarn",
  package: {
    name: npmConfig.name,
    version: npmConfig.version,
    description: npmConfig.description,
    license: "MIT",
    repository: {
      type: "git",
      url: "git+https://github.com/BioforestChain/plaoc.git",
    },
    bugs: {
      url: "https://github.com/BioforestChain/plaoc/issues",
    },
    dependencies: {
      // 不能在import_map中依赖，否则会被tree shaking。如果引用，则会报错。
      "@bfsx/sw": "^1.0.0",
    },
    devDependencies: {
      "@types/node": "latest",
      "@types/tar": "^6.1.3",
      "@types/inquirer": "^9.0.2",
      "@bfsx/typings": "^0.0.7",
      "@bfsx/metadata": "^0.0.7",
    },
  },
};

if (import.meta.main) {
  emptyDir("./.npm");
  await build(buildOptions);
  await Deno.copyFile("./LICENSE", "./.npm/LICENSE");
  await Deno.copyFile("./README.md", "./.npm/README.md");
}
