import { js_to_rust_buffer, eval_js } from "./rust.op.mjs";
const versionView = new Uint8Array(new ArrayBuffer(1));
const headView = new Uint8Array(new ArrayBuffer(2));
versionView[0] = 1;
class Deno {
  createHeader() {
    const tail = headView.length - 1;
    headView[tail] += 1;
    if ((headView[tail] & 255) === 255) {
      this.bitLeftShifts();
      headView[tail] = 0;
    }
  }
  callFunction(handleFn, data = "''") {
    const uint8Array = this.structureBinary(handleFn, data);
    js_to_rust_buffer(uint8Array);
  }
  callEvalJsStringFunction(handleFn, data = "''") {
    const uint8Array = this.structureBinary(handleFn, data);
    eval_js(uint8Array);
  }
  callEvalJsByteFunction(handleFn, data = "''") {
    const buffer = new TextEncoder().encode(data);
    const uint8Array = this.structureBinary(handleFn, buffer);
    eval_js(uint8Array);
  }
  structureBinary(fn, data = "") {
    const message = `{"function":["${fn}"],"data":${data}}`;
    const encoder = new TextEncoder();
    const uint8Array = encoder.encode(message);
    return this.concatenate(versionView, headView, uint8Array);
  }
  bitLeftShifts() {
    let rest = 0;
    for (let i = headView.length - 1; i >= 0; i--) {
      const v = headView[i];
      const newRest = (v & 128) > 0 ? 1 : 0;
      headView[i] = (v << 1 | rest) & 255;
      rest = newRest;
    }
  }
  concatenate(...arrays) {
    let totalLength = 0;
    for (let arr of arrays) {
      totalLength += arr.length;
    }
    const result = new Uint8Array(totalLength);
    let offset = 0;
    for (let arr of arrays) {
      result.set(arr, offset);
      offset += arr.length;
    }
    return result;
  }
}
const deno = new Deno();
export { deno };
//# sourceMappingURL=index.mjs.map
