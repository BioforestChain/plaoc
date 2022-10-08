// your OS.
import "@bfsx/typings";
/**js 到rust的消息 */
export function js_to_rust_buffer(data: Uint8Array) {
  Deno.core.opSync("op_js_to_rust_buffer", data);
}
/**js 到rust的消息： 调用android方法执行evenjs，即传递消息给前端 */
export function eval_js(data: Uint8Array) {
  Deno.core.opSync("op_eval_js", data);
}

/**循环从rust里拿数据 */
export function loopRustBuffer(opFunction: string) {
  return {
    async next() {
      let buffer: number[] = [];
      let versionView;
      let headView;
      try {
        buffer = await Deno.core.opAsync(opFunction);
        console.log("rust发送消息给deno_js:", buffer);
        if (buffer[0] === 0) {
          versionView = buffer.splice(0, 1); //拿到版本号
          headView = buffer.splice(0, 2); // 拿到头部标记
        }
        const string = new TextDecoder().decode(new Uint8Array(buffer));
        console.log("rust发送消息给deno_js:", string);
        return {
          value: string,
          versionView,
          headView,
          done: false,
        };
      } catch (_e) {
        return {
          value: "",
          done: true,
        };
      }
    },
    return() {
      /// 这里可以手动控制异步迭代器被 “中止” 释放
    },
    throw() {
      /// 这里可以手动控制异步迭代器被 “中止” 释放
    },
  };
}

