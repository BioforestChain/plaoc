import { defineWorkspace } from "@bfchain/pkgm-bfsw";

import typings from "./typings/#bfsp";
import util from "./util/#bfsp";
import topbar from "./topbar/#bfsp";
import icon from "./icon/#bfsp";
import bottombar from "./bottombar/#bfsp";
import statusbar from "./statusbar/#bfsp";
import dialogs from "./dialogs/#bfsp";
import keyboard from "./keyboard/#bfsp";

export default defineWorkspace(() => {
  const config: Bfsw.Workspace = {
    projects: [
      typings,
      util,
      icon,
      topbar,
      bottombar,
      statusbar,
      dialogs,
      keyboard,
      //
    ],
  };

  return config;
});
