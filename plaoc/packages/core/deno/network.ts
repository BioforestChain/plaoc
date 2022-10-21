// deno-lint-ignore-file no-async-promise-executor
/////////////////////////////
/// 这里封装后端调用的异步方法
/////////////////////////////

import { callDeno, callDVebView } from "./android.fn.ts";
import { Deno } from "./Deno.ts"
import { loopRustBuffer } from "./rust.op.ts";

const deno = new Deno();
class Network {
  private isWaitingData = 0;
  private pool: string[] = [];
  /**反压高水位，暴露给开发者控制 */
  hightWaterMark = 10;

  /**
 * 异步调用方法,这个是给后端调用的方法，不会传递数据到前端
 * @param handleFn 
 * @param data 
 * @returns 
 */
  // deno-lint-ignore no-explicit-any
  asyncCallDenoFunction(handleFn: string, data: TNative = "''", timeout = 3000): Promise<any> {
    return new Promise(async (resolve, reject) => {
      if (data instanceof Object) {
        data = JSON.stringify(data); // stringify 两次转义一下双引号
      }
      const { headView, msg } = await deno.callFunction(handleFn, JSON.stringify(data)) // 发送请求
      // 如果直接有msg返回，那么就代表非denoRuntime环境
      if (msg) {
        return resolve(msg);
      }
      do {
        const data = await loopRustBuffer("op_rust_to_js_system_buffer").next();
        if (data.done) {
          continue;
        }
        console.log("asyncCallDenoFunction headView  ====> ", data.value);
        console.log("data.headView:", data.headView, " xxxx ", headView)
        // 如果请求是返回了是同一个表示头则返回成功
        const isCur = data!.headView.filter((byte, index) => {
          return byte === Array.from(headView)[index]
        })
        if (isCur.length === 2) {
          resolve(data.value);
          break;
        }
      } while (true);
      setTimeout(() => {
        reject("call function timeout")
      }, timeout);
    })
  }

  /**
* 同步调用方法没返回值
* @param handleFn 
* @param data 
*/
  syncCallDenoFunction(handleFn: string, data?: string): void {
    deno.callFunction(handleFn, data) // 发送请求
  }

  /**
   * 轮询向rust拿数据，路径为：dwebView-js-(fetch)->kotlin-(ffi)->rust-(op)->deno-js->kotlin(eventJs)->dwebView-js
   * 这里是接收dwebView-js操作系统API转发到后端的请求
  */
  async waterOverflow() {
    do {
      const data = await loopRustBuffer("op_rust_to_js_buffer").next();
      /// 忽略策略:如果队列大于高水位或者没有数据，直接忽略
      if (data.done || this.isWaitingData > this.hightWaterMark) {
        continue;
      }
      console.log("waterOverflow====>", data.value);
      try {
        /// 如果是操作对象，拿出对象的操作函数和数据,传递给Kotlin
        const handler = JSON.parse(data.value);
        // 保证存在操作函数中
        if (Object.values(callDeno).includes(handler.function)) {
          this.callKotlinFactory(handler);
          continue;
        }
      } catch (_e) {
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
    this.isWaitingData++; // 
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
    this.isWaitingData--; // 完成闭环，减少一个等待数
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
      `"javascript:document.querySelector('${wb}').dispatchStringMessage('${data}')"`,
    );
  }

}
// deno-lint-ignore ban-types
type TNative = boolean | object | string | number;

export const network = new Network()
