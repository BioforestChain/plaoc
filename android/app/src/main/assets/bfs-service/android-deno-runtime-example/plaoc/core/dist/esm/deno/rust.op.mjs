function js_to_rust_buffer(data) {
  Deno.core.opSync("op_js_to_rust_buffer", data);
}
function eval_js(data) {
  Deno.core.opSync("op_eval_js", data);
}
function loopRustBuffer() {
  return {
    async next() {
      let buffer = [];
      try {
        buffer = await Deno.core.opAsync("op_rust_to_js_buffer");
        console.log("rust\u53D1\u9001\u6D88\u606F\u7ED9deno_js:", buffer);
        if (buffer[0] === 0) {
          buffer.splice(0, 3);
        }
        const string = new TextDecoder().decode(new Uint8Array(buffer));
        console.log("rust\u53D1\u9001\u6D88\u606F\u7ED9deno_js:", string);
        return {
          value: string,
          done: false
        };
      } catch (e) {
        return {
          value: "",
          done: true
        };
      }
    },
    return() {
    },
    throw() {
    }
  };
}
export { eval_js, js_to_rust_buffer, loopRustBuffer };
//# sourceMappingURL=rust.op.mjs.map
