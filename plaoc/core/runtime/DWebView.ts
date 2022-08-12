import { callDeno, callDVebView } from "../deno/android.fn";
import { deno } from "../deno/index";
import { MetaData } from "@bfsx/metadata";
import { loopRustBuffer } from "../deno/rust.op";

export class DWebView {
  private isWaitingData = 0;
  private pool: string[] = [];
  /**反压高水位，暴露给开发者控制 */
  hightWaterMark = 10;
  url!: string;
  entry: string;
  constructor(metaData: MetaData) {
    this.entry = metaData.manifest.enter;
    this.url = metaData.baseUrl;
    this.initAppMetaData(metaData);
    deno.createHeader();
    this.waterOverflow();
  }
  /**轮询向rust拿数据，路径为：dwebView-js-(fetch)->kotlin-(ffi)->rust-(op)->deno-js*/
  async waterOverflow() {
    do {
      const data = await loopRustBuffer().next();
      /// 忽略策略:如果队列大于高水位或者没有数据，直接忽略
      if (data.done || this.isWaitingData > this.hightWaterMark) {
        continue;
      }
      console.log("waterOverflow====>", data.value);
      try {
        /// 如果是操作对象，拿出对象的操作函数和数据，应该还会有一个channelId,传递给Kotlin
        const handler = JSON.parse(data.value);
        // 保证存在操作函数中
        if (Object.values(callDeno).includes(handler.function)) {
          this.callKotlinFactory(handler);
          continue;
        }
      } catch (e) {
        /// 如果是二进制数据的话，解析数据头和版本，拿到数据体传递给DwebView
        if (this.isWaitingData !== 0) {
          this.callDwebViewFactory(data.value);
          continue;
        }
      }
      /// 这里是重点，使用 do-while ，替代 finally，可以避免堆栈溢出。
    } while (true);
  }
  /**
   * 传递消息给DwebView-js,路径为：deno-js-(op)->rust-(ffi)->kotlin-(evaljs)->dwebView-js
   * @param wb
   * @param data
   * @returns
   */
  handlerEvalJs(wb: string, data: string) {
    // 等待数超过最高水位，操作全部丢弃
    if (this.isWaitingData > this.hightWaterMark) return;
    console.log("handlerEvalJs:", this.isWaitingData, wb, data);
    deno.callEvalJsStringFunction(
      callDeno.evalJsRuntime,
      `"javascript:document.querySelector('${wb}').dispatchStringMessage('${data}')"`
    );
  }
  /**
   * 初始化app元数据
   * @param metaData  元数据
   * @returns void
   */
  initAppMetaData(metaData: MetaData) {
    if (Object.keys(metaData).length === 0) return;
    deno.callFunction(callDeno.initMetaData, `'${JSON.stringify(metaData)}'`);
  }
  /**
   * 激活DwebView
   * @param entry // DwebView入口
   */
  activity() {
    deno.callFunction(
      callDeno.openDWebView,
      `"${new URL(this.entry, this.url).href}"`
    );
  }
  /**
   * 操作传递到kotlin
   * @param handler
   * @returns
   */
  callKotlinFactory(handler: {
    function: string;
    data: string;
    channelId: string;
  }) {
    deno.callFunction(handler.function, handler.data);
    this.pool.push(handler.function);
    if (this.isWaitingData >= Number.MAX_SAFE_INTEGER) return;
    this.isWaitingData++;
  }
  /**
   * 数据传递到DwebView
   * @param data
   * @returns
   */
  callDwebViewFactory(data: string) {
    const handler = this.pool.shift() as keyof typeof callDVebView;
    if (handler && callDVebView[handler]) {
      this.handlerEvalJs(callDVebView[handler], data);
    }
    if (this.isWaitingData === 0) return;
    this.isWaitingData--;
  }
}
