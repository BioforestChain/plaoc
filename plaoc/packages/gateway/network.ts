// deno-lint-ignore-file no-explicit-any
/// <reference lib="dom" />
/**
 * 注册serverWorker方法
 */
export function registerServerWorker() {
  addEventListener("load", () => {
    // 能力检测
    if ("serviceWorker" in navigator) {
      navigator.serviceWorker.register("serverWorker.js", { scope: "/" }).then(
        () => {
          console.log("Service Worker register success");
        },
      ).catch(() => {
        console.log("Service Worker register error");
      });
    }
  });
}

/**
 *  发送请求到netive设置ui
 * @param url
 * @returns
 */
export function netCallNativeUi(
  fun: string,
  data: TNative = "",
): Promise<any> {
  if (data instanceof Object) {
    data = JSON.stringify(data); // stringify 两次转义一下双引号
  }
  const message = `{"function":"${fun}","data":${JSON.stringify(data)}}`;
  const buffer = new TextEncoder().encode(message);
  return getConnectChannel(`/setUi?data=${buffer}`);
}


/**
 *  发送请求到netive设置ui
 * @param url
 * @returns
 */
export function netCallNativeVfs(
  fun: string,
  data: TNative = "",
): Promise<string> {
  if (data instanceof Object) {
    data = JSON.stringify(data); // stringify 两次转义一下双引号
  }
  const message = `{"function":"${fun}","data":${JSON.stringify(data)}}`;
  const buffer = new TextEncoder().encode(message);
  return getConnectChannel(`/setVfs?data=${buffer}`);
}

// deno-lint-ignore ban-types
type TNative = boolean | object | string | number;

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

/**
 * 请求kotlin 代理转发 POST
 * @param url
 * @returns 直接返回ok
 */

export async function postConnectChannel(url: string) {
  const response = await fetch(url, {
    method: "POST", // dwebview 无法获取post的body,曲线救国，发送到serverWorker去处理成数据片。
    headers: {
      "Access-Control-Allow-Origin": "*", // 客户端开放，不然会报cors
      "Content-Type": "application/json",
    },
    mode: "cors",
  });
  const data = await response.text();
  return data;
}
