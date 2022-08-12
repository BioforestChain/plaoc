import { defineConfig } from "@bfchain/pkgm-bfsp";
export default defineConfig((info) => {
  const config: Bfsp.UserConfig = {
    name: "@plaoc/test",
    exports: {
      ".": "./index.ts",
    },
    deps: [
      "@plaoc/plugin-typings",
      "@plaoc/plugin-icon",
      "@plaoc/plugin-top-bar-android",
      "@plaoc/plugin-bottom-bar-android",
      "@plaoc/plugin-status-bar-android",
      "@plaoc/plugin-dialogs-android",
      "@plaoc/plugin-keyboard-android",
      // "@plaoc/plugin-top-bar-ios",
      // "@plaoc/plugin-bottom-bar-ios",
      // "@plaoc/plugin-status-bar-ios",
      // "@plaoc/plugin-dialogs-ios",
      // "@plaoc/plugin-keyboard-ios",
    ],
    packageJson: {
      license: "MIT",
      author: "bnqkl",
    },
  };
  return config;
});
