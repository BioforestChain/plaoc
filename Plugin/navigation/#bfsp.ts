import { defineConfig } from "@bfchain/pkgm-bfsp";
export default defineConfig((info) => {
  return {
    name: "@plaoc/plugin-navigation",
    profiles: ["android"],
    exports: {
      ".": "./index.ts",
    },
  };
});
