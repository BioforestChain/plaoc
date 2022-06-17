import { defineConfig } from "@bfchain/pkgm-bfsp";
export default defineConfig((info) => {
  const config: Bfsp.UserConfig = {
    name: "@plaoc/test",
    exports: {
      ".": "./index.ts",
    },
    deps: ["@plaoc/plugin-icon", "@plaoc/plugin-top-bar"],
    packageJson: {
      license: "MIT",
      author: "bnqkl",
    },
  };
  return config;
});
