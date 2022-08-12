/**
 * 所有的dweb-plugin需要继承这个类
 */
export class DwebPlugin extends HTMLElement {
  private isWaitingData = 0;
  asyncDataArr: string[] = []; // 存储迭代目标
  /**反压高水位，暴露给开发者控制 */
  hightWaterMark = 10;
  /** 用来区分不同的Dweb-plugin建议使用英文单词，单元测试需要覆盖中文和特殊字符传输情况*/
  channelId = "";
  constructor() {
    super();
  }
  /**接收kotlin的evaJs来的string */
  dispatchStringMessage = (data: string) => {
    console.log("dweb-plugin dispatchStringMessage:", data);
    if (this.isWaitingData > this.hightWaterMark) {
      return;
    }
    this.isWaitingData++;
    this.asyncDataArr.push(data);
  };
  /**接收kotlin的evaJs来的buffer，转为string */
  dispatchBinaryMessage = (buf: ArrayBuffer) => {
    console.log("dweb-plugin dispatchBinaryMessage:", buf);
    const data = new TextDecoder("utf-8").decode(new Uint8Array(buf)); // 需要测试特殊字符和截断问题
    console.log("dweb-plugin dispatchBinaryMessage:", data);
    this.asyncDataArr.push(data);
  };
  /**迭代器生成函数*/
  onMesage() {
    return {
      next: async () => {
        const data = this.asyncDataArr.shift();
        if (data) {
          return {
            value: data,
            done: false,
          };
        }
        return { value: "", done: true };
      },
    };
  }
  /**
   * 发送请求get Kotlin 转发
   * @param fun 操作函数
   * @param data 数据
   * @returns Promise<Ok>
   */
  async onPolling(fun: string, data: string = `"''"`): Promise<string> {
    const message = `{"function":"${fun}","data":${data},"channelId":"${this.channelId}"}`;
    const buffer = new TextEncoder().encode(message);
    return this.connectChannel(`/poll?data=${buffer}`);
  }
  /**
   * 请求kotlin 代理转发
   * @param url
   * @returns 直接返回ok
   */
  async connectChannel(url: string) {
    const response = await fetch(url, {
      method: "GET", // dwebview 无法获取post的body
      headers: {
        "Access-Control-Allow-Origin": "*", // 客户端开放，不然会报cors
        "Content-Type": "application/json",
      },
      mode: "cors",
    });
    const data = await response.text();
    console.log(data);
    return data;
  }
  /**返回需要监听的属性 */
  static get observedAttributes() {
    return ["channelId"]; // 用来区分多个组件
  }
  /**当属性值改变的时候会调用 attributeChangedCallback 这个我 */
  attributeChangedCallback(name: string, oldValue: string, newValue: string) {
    console.log("channelId: ", name, oldValue, newValue);
    if (name === "channelId") {
      this.channelId = newValue;
    }
  }
}

type Interator<T> = {
  next: () => Promise<{ value: T; done: boolean }>;
};
