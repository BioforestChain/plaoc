import { callDeno } from "../deno/android.fn";
import { deno } from "../deno/index";

/**
 * 打开扫码
 * @returns Promise<data>
 */
export const openScanner = () => {
  return new Promise((resolve, reject) => {
    deno.callFunction(callDeno.openScanner);
  });
};
