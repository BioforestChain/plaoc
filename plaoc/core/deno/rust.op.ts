// your OS.
import "@bfsx/typings";

export function js_to_rust_buffer(data: Uint8Array) {
  Deno.core.opSync("op_js_to_rust_buffer", data);
}

export function eval_js(data: Uint8Array) {
  Deno.core.opSync("op_eval_js", data);
}

/**循环从rust里拿数据 */
export function loopRustBuffer() {
  return {
    async next() {
      let buffer: number[] = [];
      try {
        buffer = await Deno.core.opAsync("op_rust_to_js_buffer");
        console.log("rust发送消息给deno_js:", buffer);
        if (buffer[0] === 0) {
          buffer.splice(0, 3);
        }
        const string = new TextDecoder().decode(new Uint8Array(buffer));
        console.log("rust发送消息给deno_js:", string);
        return {
          value: string,
          done: false,
        };
      } catch (e) {
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

/**迭代器生成函数*/
// function asyncLoopIterator() {
//   return {
//     [Symbol.asyncIterator]: () => {
//       return {
//         next: () => loopRustBuffer(),
//       };
//     },
//   };
// }

// asyncIterator
// export function loopRustBuffer() {
//   setInterval(() => {
//     let buffer = rust_to_js_buffer();
//     console.log(" rust发送消息给deno_js", buffer);
//   }, 1000);
// }
// loopRustBuffer();
// export function getEnv(key: string) {
//   console.log("执行了op_get_env:", key);
//   return Deno.core.opSync("op_get_env", key) ?? undefined;
// }
// Deno.env.set("rust_to_js_buffer", "rust_to_js_buffer");
// console.log("hahahaha:", getEnv("rust_to_js_buffer"));

// Deno.core.setUncaughtExceptionCallback;

// const dylib = Deno.dlopen(libName, {
//   // deno_js向rust发送消息
//   js_to_rust_buffer: { parameters: ["pointer", "usize"], result: "void" },
//   // rust发送消息给deno_js
//   rust_to_js_buffer: {
//     parameters: ["pointer"],
//     result: "pointer",
//   },
//   // deno_js 走这个ffi去执行kotlin 的evaljs通知dwebview-js
//   eval_js: { parameters: ["pointer", "usize"], result: "void" },
//   // store_function: { parameters: ["function"], result: "pointer" },
// });
