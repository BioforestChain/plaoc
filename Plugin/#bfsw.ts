import { defineWorkspace } from "@bfchain/pkgm-bfsw";

import bottombar from "./bottombar/#bfsp";
import dialogs from "./dialogs/#bfsp";
import keyboard from "./keyboard/#bfsp";
import statusbar from "./statusbar/#bfsp";
import topbar from "./topbar/#bfsp";
import icon from "./icon/#bfsp";

export default defineWorkspace(() => {
  const config: Bfsw.Workspace = {
    projects: [
      // bottombar,
      // dialogs,
      // keyboard,
      // statusbar,
      topbar,
      icon,
    ],
  };

  return config;
});
