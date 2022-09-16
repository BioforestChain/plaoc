

/**
 * 注册serverWorker方法
 */
export function registerServerWorker() {
  console.log("Xxxx")
  window.addEventListener("load", () => {
    // 能力检测
    if ("serviceWorker" in navigator) {
      navigator.serviceWorker.register("serverWorker.mjs", { scope: '/' }).then((res) => {
        console.log('Service Worker 注册成功');
      }).catch(() => {
        console.log('Service Worker 注册失败');
      });
    }
  });

}

/**
 *  发送请求到netive设置ui
 * @param url 
 * @returns 
 */
export async function netCallNative(fun: string, data: TNative = ""): Promise<any> {
  if (data instanceof Object) {
    data = JSON.stringify(data) // stringify 两次转义一下双引号
  }
  const message = `{"function":"${fun}","data":${JSON.stringify(data)}}`;
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
