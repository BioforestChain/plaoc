import { defineConfig } from "@bfchain/pkgm-bfsp";
export default defineConfig((info) => {
  const config: Bfsp.UserConfig = {
    name: "@plaoc/plugin-top-bar",
    profiles: ["android", "ios", "default"],
    exports: {
      ".": "./index.ts",
    },
    deps: ["@plaoc/plugin-icon", "@plaoc/plugin-typings", "@plaoc/plugin-util"],
    packageJson: {
      license: "MIT",
      author: "bnqkl",
    },
    build: [
      {
        name: "@plaoc/plugin-top-bar-android",
        profiles: ["android"],
      },
      {
        name: "@plaoc/plugin-top-bar-ios",
        profiles: ["ios"],
      },
    ],
  };
  return config;
});
