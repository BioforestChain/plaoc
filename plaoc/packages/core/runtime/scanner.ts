import { callDeno } from "../deno/android.fn.ts";
import { deno } from "../deno/index.ts";

/**
 * 打开二维码扫码
 * @returns Promise<data>
 */
export const openQrScanner = () => {
  return new Promise(() => {
    deno.callFunction(callDeno.openQrScanner);
  });
};

/**
 * 打开条形码扫码
 * @returns Promise<data>
 */
export const openBarScanner = () => {
  return new Promise(() => {
    deno.callFunction(callDeno.openBarcodeScanner);
  });
};
