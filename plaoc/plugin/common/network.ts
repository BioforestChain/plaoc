
/**
 *  发送请求到netive设置ui
 * @param url 
 * @returns 
 */
export async function netCallNative(fun: string, data: TNative = ""): Promise<any> {
  const message = `{"function":"${fun}","data":'${JSON.stringify(data)}'}`;
  const buffer = new TextEncoder().encode(message);
  return connectChannel(`/setUi?data=${buffer}`);
}

type TNative = boolean | object | string | number;

/**
   * 请求kotlin 代理转发
   * @param url
   * @returns 直接返回ok
   */

export async function connectChannel(url: string) {
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
