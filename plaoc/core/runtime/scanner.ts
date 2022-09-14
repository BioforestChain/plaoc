import { callDeno } from "../deno/android.fn";
import { deno } from "../deno/index";

/**
 * 打开二维码扫码
 * @returns Promise<data>
 */
export const openQrScanner = () => {
  return new Promise((resolve, reject) => {
    deno.callFunction(callDeno.openQrScanner);
  });
};

/**
 * 打开条形码扫码
 * @returns Promise<data>
 */
export const openBarScanner = () => {
  return new Promise((resolve, reject) => {
    deno.callFunction(callDeno.openBarcodeScanner);
  });
};

