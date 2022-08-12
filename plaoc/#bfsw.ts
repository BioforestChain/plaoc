import { defineWorkspace } from "@bfchain/pkgm-bfsw";

import core from "./core/#bfsp";
import plugin from "./plugin/#bfsp";
import typings from "./typings/#bfsp";
import metadata from "./metadata/#bfsp";

export default defineWorkspace(() => {
  const config: Bfsw.Workspace = {
    projects: [core, plugin, metadata, typings],
  };

  return config;
});
