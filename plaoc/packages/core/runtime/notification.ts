import { callDeno } from "../deno/android.fn.ts";
import { netCallNativeService } from '@bfsx/gateway';
import { setNotification } from "../deno/rust.op.ts"
import { isAndroid } from "./device.ts";


/**
 * 发送通知
 * @param data 
 * @returns 
 */
export function sendNotification(data: Uint8Array) {
  if (isAndroid) {
    return setNotification(data)
  }
  return sendNetNotification(data)
}


/**
 *  非 denoRutime环境发送通知
 * @param data 
 * @returns 
 */
export async function sendNetNotification(data: Uint8Array) {
  const info = await netCallNativeService(callDeno.sendNotification, data);
  return info
}
