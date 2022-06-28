import { defineWorkspace } from "@bfchain/pkgm-bfsw";

import typings from "./typings/#bfsp";
import topbar from "./topbar/#bfsp";
import icon from "./icon/#bfsp";
import bottombar from "./bottombar/#bfsp";
import dialogs from "./dialogs/#bfsp";
import keyboard from "./keyboard/#bfsp";
import statusbar from "./statusbar/#bfsp";

export default defineWorkspace(() => {
  const config: Bfsw.Workspace = {
    projects: [
      typings,
      icon,
      topbar,
      bottombar,
      // dialogs,
      // keyboard,
      // statusbar,
      //
    ],
  };

  return config;
});
