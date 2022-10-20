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
      tar: "^6.1.11",
      commander: "^9.4.0",
      "@bfsx/gateway": "^0.0.4",
    },
    devDependencies: {
      "@types/node": "latest",
      "@types/tar": "^6.1.3",
      "@bfsx/typings": "^0.0.3",
      "@bfsx/metadata": "^0.0.4",
    },
  },
};

if (import.meta.main) {
  emptyDir("./.npm");
  await build(buildOptions);
  await Deno.copyFile("./LICENSE", "./.npm/LICENSE");
  await Deno.copyFile("./README.md", "./.npm/README.md");
}
