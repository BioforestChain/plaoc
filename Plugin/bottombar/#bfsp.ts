import { defineConfig } from "@bfchain/pkgm-bfsp";
export default defineConfig((info) => {
  const config: Bfsp.UserConfig = {
    name: "@plaoc/plugin-bottom-bar",
    profiles: ["android", "ios", "desktop", "default"],
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
        name: "@plaoc/plugin-bottom-bar-android",
        profiles: ["android"],
      },
      {
        name: "@plaoc/plugin-bottom-bar-ios",
        profiles: ["ios"],
      },
      {
        name: "@plaoc/plugin-bottom-bar-desktop",
        profiles: ["desktop"],
        tsConfig: {
          compilerOptions: {
            module: "ES2022",
            target: "ES2022",
          },
        },
      },
    ],
  };
  return config;
});
