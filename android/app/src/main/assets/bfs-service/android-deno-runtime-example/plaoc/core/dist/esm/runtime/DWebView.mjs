import { callDeno, callDVebView } from "../deno/android.fn.mjs";
import { deno } from "../deno/index.mjs";
import { loopRustBuffer } from "../deno/rust.op.mjs";
class DWebView {
  constructor(metaData) {
    this.isWaitingData = 0;
    this.pool = [];
    this.hightWaterMark = 10;
    this.entry = metaData.manifest.enter;
    this.url = metaData.baseUrl;
    this.initAppMetaData(metaData);
    deno.createHeader();
    this.waterOverflow();
  }
  async waterOverflow() {
    do {
      const data = await loopRustBuffer().next();
      if (data.done || this.isWaitingData > this.hightWaterMark) {
        continue;
      }
      console.log("waterOverflow====>", data.value);
      try {
        const handler = JSON.parse(data.value);
        if (Object.values(callDeno).includes(handler.function)) {
          this.callKotlinFactory(handler);
          continue;
        }
      } catch (e) {
        if (this.isWaitingData !== 0) {
          this.callDwebViewFactory(data.value);
          continue;
        }
      }
    } while (true);
  }
  handlerEvalJs(wb, data) {
    if (this.isWaitingData > this.hightWaterMark)
      return;
    console.log("handlerEvalJs:", this.isWaitingData, wb, data);
    deno.callEvalJsStringFunction(callDeno.evalJsRuntime, `"javascript:document.querySelector('${wb}').dispatchStringMessage('${data}')"`);
  }
  initAppMetaData(metaData) {
    if (Object.keys(metaData).length === 0)
      return;
    deno.callFunction(callDeno.initMetaData, `'${JSON.stringify(metaData)}'`);
  }
  activity() {
    deno.callFunction(callDeno.openDWebView, `"${new URL(this.entry, this.url).href}"`);
  }
  callKotlinFactory(handler) {
    deno.callFunction(handler.function, handler.data);
    this.pool.push(handler.function);
    if (this.isWaitingData >= Number.MAX_SAFE_INTEGER)
      return;
    this.isWaitingData++;
  }
  callDwebViewFactory(data) {
    const handler = this.pool.shift();
    if (handler && callDVebView[handler]) {
      this.handlerEvalJs(callDVebView[handler], data);
    }
    if (this.isWaitingData === 0)
      return;
    this.isWaitingData--;
  }
}
export { DWebView };
//# sourceMappingURL=DWebView.mjs.map
