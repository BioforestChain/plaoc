/// <reference lib="dom" />
import { DwebPlugin } from "./dweb-plugin";

export class DWebMessager extends DwebPlugin {
  constructor() {
    super();
  }
}

export class DWebView extends DwebPlugin {
  constructor() {
    super();
  }
}

export class OpenScanner extends DwebPlugin {
  constructor() {
    super();
  }
  async openScanner(): Promise<string> {
    const ok = await this.onPolling("openScanner");
    if (ok !== "ok") {
      throw new Error("打开扫码失败！"); // todo 记录日志
    }
    let index = 1;
    return new Promise(async (resolve, reject) => {
      do {
        const data = await this.onMesage().next();
        if (data.done === false) {
          resolve(data.value);
          break;
        }
        index++;
        await loop(500);
      } while (index < 10);
    });
  }
  // dom被删除的声明周期
  disconnectedCallback() {
    // this.onClose();
  }
}

const loop = (delay: number) =>
  new Promise((resolve) => setTimeout(resolve, delay));

/**
 * 服务端的用户如果想给全部的dweb-plugin发送广播，需要在evalJs调用dwebPlugin.dispatch
 * 单独给某个webComponent发送消息则使用 组件名称.dispatch，
 * 单元测试需要使用模拟函数覆盖到两者所有组件
 */
customElements.define("dweb-messager", DWebMessager);
customElements.define("dweb-view", DWebView);
customElements.define("dweb-scanner", OpenScanner);
