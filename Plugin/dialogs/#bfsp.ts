import { defineConfig } from "@bfchain/pkgm-bfsp";
export default defineConfig((info) => {
  const config: Bfsp.UserConfig = {
    name: "@plaoc/plugin-dialogs",
    profiles: ["android", "ios", "default"],
    exports: {
      ".": "./index.ts",
    },
    deps: ["@plaoc/plugin-typings"],
    packageJson: {
      license: "MIT",
      author: "bnqkl",
    },
    build: [
      {
        name: "@plaoc/plugin-dialogs-android",
        profiles: ["android"],
      },
      {
        name: "@plaoc/plugin-dialogs-ios",
        profiles: ["ios"],
      },
    ],
  };
  return config;
});
