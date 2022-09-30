import * as process from "node_process";
import * as mod from "std_path";

export const path = process.platform === "win32" ? mod.win32 : mod.posix;
