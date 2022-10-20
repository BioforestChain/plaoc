import { callDeno } from "../deno/android.fn.ts";
import { isDeno } from "../deno/Deno.ts";
import { netCallNativeService } from "../deno/net.op.ts";
import { setNotification } from "../deno/rust.op.ts"


/**
 * 发送通知
 * @param data 
 * @returns 
 */
export function sendNotification(data: Uint8Array) {
  if (isDeno) {
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
  const info = await netCallNativeService(callDeno.createNotificationMsg, data);
  return info
}
