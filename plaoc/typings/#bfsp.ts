import { defineConfig } from "@bfchain/pkgm-bfsp";
export default defineConfig((info) => {
  const config: Bfsp.UserConfig = {
    name: "@bfsx/typings",
    exports: {
      ".": "./index.ts",
    },
    packageJson: {
      license: "MIT",
      author: "@bfchain",
      version: "0.0.2",
      private: false,
      devDependencies: {
        // "@bfsx/typings": "./",
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
