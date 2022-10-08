import { callDeno } from "../deno/android.fn.js";
import { network } from '../deno/network.js';
/**
 * 打开二维码扫码
 * @returns Promise<data>
 */
export const openQrScanner = () => {
    return new Promise(() => {
        network.syncCallDenoFunction(callDeno.openQrScanner);
    });
};
/**
 * 打开条形码扫码
 * @returns Promise<data>
 */
export const openBarScanner = () => {
    return new Promise(() => {
        network.syncCallDenoFunction(callDeno.openBarcodeScanner);
    });
};
