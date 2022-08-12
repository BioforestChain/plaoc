import { defineConfig } from "@bfchain/pkgm-bfsp";
export default defineConfig((info) => {
  const config: Bfsp.UserConfig = {
    name: "@bfsx/metadata",
    exports: {
      ".": "./index.ts",
    },
    profiles: ["android"],
    packageJson: {
      // types: "./metadata.d.ts", //
      license: "MIT",
      author: "@bfchain",
      version: "0.0.2",
      private: false,
    },
  };
  return config;
});
