import { defineConfig } from "@bfchain/pkgm-bfsp";
export default defineConfig((info) => {
  const config: Bfsp.UserConfig = {
    name: "@plaoc/plugin-status-bar",
    profiles: ["android", "ios", "desktop", "default"],
    exports: {
      ".": "./index.ts",
    },
    deps: ["@plaoc/plugin-util", "@plaoc/plugin-typings"],
    packageJson: {
      license: "MIT",
      author: "bnqkl",
    },
    build: [
      {
        name: "@plaoc/plugin-status-bar-android",
        profiles: ["android"],
      },
      {
        name: "@plaoc/plugin-status-bar-ios",
        profiles: ["ios"],
      },
      {
        name: "@plaoc/plugin-status-bar-desktop",
        profiles: ["desktop"],
      },
    ],
  };
  return config;
});
