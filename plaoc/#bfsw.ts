import { defineWorkspace } from "@bfchain/pkgm-bfsw";

import core from "./core/#bfsp";
import plugin from "./plugin/#bfsp";
import typings from "./typings/#bfsp";
import metadata from "./metadata/#bfsp";
import vfs from "./vfs/#bfsp";

export default defineWorkspace(() => {
  const config: Bfsw.Workspace = {
    projects: [core, plugin, metadata, vfs, typings],
  };

  return config;
});
