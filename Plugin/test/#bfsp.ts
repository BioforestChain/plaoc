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
      // "@plaoc/plugin-top-bar-android",
      // "@plaoc/plugin-bottom-bar-android",
      // "@plaoc/plugin-status-bar-android",
      // "@plaoc/plugin-dialogs-android",
      // "@plaoc/plugin-keyboard-android",
      // "@plaoc/plugin-top-bar-ios",
      // "@plaoc/plugin-bottom-bar-ios",
      // "@plaoc/plugin-status-bar-ios",
      // "@plaoc/plugin-dialogs-ios",
      // "@plaoc/plugin-keyboard-ios",
      "@plaoc/plugin-top-bar-desktop",
      "@plaoc/plugin-bottom-bar-desktop",
      "@plaoc/plugin-status-bar-desktop",
      // "@plaoc/plugin-dialogs-desktop",
      // "@plaoc/plugin-keyboard-desktop",
    ],
    tsConfig: {
      compilerOptions: {
        module: "ES2022",
        target: "ES2022",
      },
    },
    packageJson: {
      license: "MIT",
      author: "bnqkl",
    },
  };
  return config;
});
