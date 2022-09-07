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
    return this.onPolling("OpenScanner");
  }
  // dom被删除的声明周期
  disconnectedCallback() {
    // this.onClose();
  }
}



/**
 * 服务端的用户如果想给全部的dweb-plugin发送广播，需要在evalJs调用dwebPlugin.dispatch
 * 单独给某个webComponent发送消息则使用 组件名称.dispatch，
 * 单元测试需要使用模拟函数覆盖到两者所有组件
 */
customElements.define("dweb-messager", DWebMessager);
customElements.define("dweb-view", DWebView);
customElements.define("dweb-scanner", OpenScanner);
