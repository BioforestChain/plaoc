import { defineConfig } from "@bfchain/pkgm-bfsp";
export default defineConfig((info) => {
  const config: Bfsp.UserConfig = {
    name: "@plaoc/plugin-keyboard",
    profiles: ["android"],
    exports: {
      ".": "./index.ts",
    },
    packageJson: {
      license: "MIT",
      author: "bnqkl",
    },
  };
  return config;
});
