import { defineConfig } from "@bfchain/pkgm-bfsp";
export default defineConfig((info) => {
  const config: Bfsp.UserConfig = {
    name: "@plaoc/plugin-keyboard",
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
        name: "@plaoc/plugin-keyboard-android",
        profiles: ["android"],
      },
      {
        name: "@plaoc/plugin-keyboard-ios",
        profiles: ["ios"],
      },
    ],
  };
  return config;
});
