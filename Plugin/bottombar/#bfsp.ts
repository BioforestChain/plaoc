import { defineConfig } from "@bfchain/pkgm-bfsp";
export default defineConfig((info) => {
  const config: Bfsp.UserConfig = {
    name: "@plaoc/plugin-bottom-bar",
    profiles: ["android"],
    exports: {
      ".": "./index.ts",
    },
    deps: ["@plaoc/plugin-icon"],
    packageJson: {
      license: "MIT",
      author: "bnqkl",
    },
  };
  return config;
});
