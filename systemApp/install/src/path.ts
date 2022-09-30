import * as process from "node_process";
import * as mod from "std_path";

export const path = process.platform === "win32" ? mod.win32 : mod.posix;

/**
 * ## slash
 * Convert Windows backslash paths to slash paths: `foo\\bar` ➔ `foo/bar`
 * @fork https://github.com/sindresorhus/slash/blob/main/index.js
 */
export function slash(path: string) {
  const isExtendedLengthPath = /^\\\\\?\\/.test(path);
  const hasNonAscii = /[^\u0000-\u0080]+/.test(path); // eslint-disable-line no-control-regex

  if (isExtendedLengthPath || hasNonAscii) {
    return path;
  }

  return path.replace(/\\+/g, "/");
}
