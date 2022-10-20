/////////////////////////////
/// ios And Desktop 的服务端请求，替代android的op
/////////////////////////////


// deno-lint-ignore ban-types
type TNative = boolean | object | string | number;

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
  return getConnectChannel(`/setService?data=${buffer}`);
}


/**
 * 请求kotlin 代理转发 GET
 * @param url
 * @returns 直接返回ok
 */

export async function getConnectChannel(url: string) {
  const response = await fetch(url, {
    method: "GET", // dwebview 无法获取post的body
    headers: {
      "Access-Control-Allow-Origin": "*", // 客户端开放，不然会报cors
      "Content-Type": "application/json",
    },
    mode: "cors",
  });
  const data = await response.text();
  return data;
}
