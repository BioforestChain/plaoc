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
        "@bfsx/core":
          "link:/Users/mac/Desktop/waterbang/project/plaoc/plaoc/core",
        "@bfsx/metadata":
          "link:/Users/mac/Desktop/waterbang/project/plaoc/plaoc/metadata",
        "@bfsx/gateway":
          "link:/Users/mac/Desktop/waterbang/project/plaoc/plaoc/gateway",
        "@bfsx/typings":
          "link:/Users/mac/Desktop/waterbang/project/plaoc/plaoc/typings",
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
