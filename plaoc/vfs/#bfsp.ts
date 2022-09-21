import { defineConfig } from "@bfchain/pkgm-bfsp";
export default defineConfig((info) => {
  const config: Bfsp.UserConfig = {
    name: "@bfsx/vfs",
    exports: {
      ".": "./index.ts",
    },
    packageJson: {
      license: "MIT",
      author: "@bfchain",
      version: "0.0.2",
      private: false,
      devDependencies: {
        "@bfsx/core": "0.0.2",
        "@bfsx/typings": "0.0.2",
      },
    },
    tsConfig: {
      compilerOptions: {
        // isolatedModules: false,
      },
    },
  };
  return config;
});
