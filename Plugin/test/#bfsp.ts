import { defineConfig } from "@bfchain/pkgm-bfsp";
export default defineConfig((info) => {
  const config: Bfsp.UserConfig = {
    name: "@plaoc/test",
    exports: {
      ".": "./index.ts",
    },
    deps: [
      "@plaoc/plugin-icon",
      "@plaoc/plugin-top-bar",
      "@plaoc/plugin-bottom-bar",
      "@plaoc/plugin-status-bar",
    ],
    packageJson: {
      license: "MIT",
      author: "bnqkl",
    },
  };
  return config;
});
