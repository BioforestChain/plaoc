import { defineConfig } from "@bfchain/pkgm-bfsp";
export default defineConfig((info) => {
  const config: Bfsp.UserConfig = {
    name: "@bfsx/plugin",
    exports: {
      ".": "./index.ts",
    },
    profiles: ["android", "ios", "desktop", "default"],
    packageJson: {
      license: "MIT",
      author: "@bfchain",
      version: "0.0.2",
      private: false,
      dependencies: {},
      devDependencies: {},
    },
  };

  return config;
});
