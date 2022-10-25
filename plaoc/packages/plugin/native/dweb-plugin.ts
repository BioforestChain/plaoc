import { loop } from "../common/index.ts";
import { createMessage } from "@bfsx/gateway";
/**
 * 所有的dweb-plugin需要继承这个类
 */
export class DwebPlugin extends HTMLElement {
  private isWaitingData = 0;
  asyncDataArr: string[] = []; // 存储迭代目标
  /**反压高水位，暴露给开发者控制 */
  hightWaterMark = 10;
  /** 用来区分不同的Dweb-plugin建议使用英文单词，单元测试需要覆盖中文和特殊字符传输情况*/
  // channelId = "";
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
      next: () => {
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
   * @param fun 操作函数
   * @param data 数据
   * @returns Promise<Ok>
   */
  async onPolling(
    fun: string,
    data = `"''"`,
    delay = 500,
  ): Promise<string> {
    const ok = await createMessage(fun, data);
    let index = 1;
    // deno-lint-ignore no-async-promise-executor
    return new Promise(async (resolve, reject) => {
      if (ok !== "ok") {
        reject(`${fun}操作失败`); // todo 记录日志
      }
      do {
        const data = await this.onMesage().next();
        if (data.done === false) {
          resolve(data.value);
          break;
        }
        index++;
        await loop(delay);
      } while (index < 10);
    });
  }

}
