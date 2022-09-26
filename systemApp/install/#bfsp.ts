import { defineConfig } from "@bfchain/pkgm-bfsp";
export default defineConfig((info) => {
  const config: Bfsp.UserConfig = {
    name: "install",
    exports: {
      ".": "./index.ts",
    },
    packageJson: {
      license: "MIT",
      author: "mac",
    },
  };
  return config;
});
