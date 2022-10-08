import { build, emptyDir, type BuildOptions } from "dnt";

const npmConfig = (await import("./npm.json", { assert: { type: "json" } }))
  .default;

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
    },
    devDependencies: {
      "@types/node": "latest",
      "@types/tar": "^6.1.3",
      "@bfsx/typings": "link:../../../plaoc/build/typings",
      "@bfsx/metadata": "link:../../../plaoc/build/metadata",
    },
  },
};

if (import.meta.main) {
  emptyDir("./.npm");
  await build(buildOptions);
}