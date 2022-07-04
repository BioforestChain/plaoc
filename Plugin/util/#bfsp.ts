import { defineConfig } from "@bfchain/pkgm-bfsp";
export default defineConfig((info) => {
  const config: Bfsp.UserConfig = {
    name: "@plaoc/plugin-util",
    exports: {
      ".": "./index.ts",
    },
    deps: ["@plaoc/plugin-typings"],
    packageJson: {
      license: "MIT",
      author: "bnqkl",
    },
  };
  return config;
});
