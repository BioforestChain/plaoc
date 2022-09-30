import * as process from "process";
import * as mod from "../deps/deno.land/std@0.157.0/path/mod.js";
export const path = process.platform === "win32" ? mod.win32 : mod.posix;
