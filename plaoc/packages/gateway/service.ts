/////////////////////////////
/// ios And Desktop 的服务端请求，替代android的op
/////////////////////////////

import { getConnectChannel } from "./network.ts";
import { TNative } from "./gateway.d.ts";

/**
 *  后端发送给ios和desktop的请求
 * @param url
 * @returns
 */
export function netCallNativeService(
  fun: string,
  data: TNative = "",
  // deno-lint-ignore no-explicit-any
): Promise<any> {
  if (data instanceof Object) {
    data = JSON.stringify(data); // stringify 两次转义一下双引号
  }
  const message = `{"function":"${fun}","data":${JSON.stringify(data)}}`;
  const buffer = new TextEncoder().encode(message);
  return getConnectChannel(`/setService=${buffer}`);
}
