import { defineConfig } from "@bfchain/pkgm-bfsp";
export default defineConfig((info) => {
  const config: Bfsp.UserConfig = {
    name: "bfs-service",
    exports: {
      ".": "./index.ts",
    },
    profiles: ["android"],
    packageJson: {
      license: "MIT",
      author: "@bfchain",
      version: "0.0.1",
      private: false,
      dependencies: {},
      devDependencies: {
        "@bfsx/core": "link:../../plaoc/core/build/@bfsx/core",
        "@bfsx/metadata": "link:../../plaoc/metadata/build/@bfsx/metadata",
        "@bfsx/typings": "link:../../plaoc/typings/build/@bfsx/typings",
      },
    },
    tsConfig: {
      compilerOptions: {
        lib: ["ES2020", "DOM"],
      },
    },
  };
  return config;
});
