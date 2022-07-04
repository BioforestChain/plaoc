import { defineConfig } from "@bfchain/pkgm-bfsp";
export default defineConfig((info) => {
  const config: Bfsp.UserConfig = {
    name: "@plaoc/test",
    exports: {
      ".": "./index.ts",
    },
    deps: [
      "@plaoc/plugin-icon",
      "@plaoc/plugin-top-bar-ios",
      // "@plaoc/plugin-bottom-bar",
      // "@plaoc/plugin-status-bar",
      // "@plaoc/plugin-dialogs",
      // "@plaoc/plugin-keyboard",
    ],
    packageJson: {
      license: "MIT",
      author: "bnqkl",
    },
  };
  return config;
});
