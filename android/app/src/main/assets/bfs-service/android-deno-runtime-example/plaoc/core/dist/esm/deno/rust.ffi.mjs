let libSuffix = "";
switch (Deno.build.os) {
  case "windows":
    libSuffix = "dll";
    break;
  case "darwin":
    libSuffix = "dylib";
    break;
  default:
    libSuffix = "so";
    break;
}
const libName = `librust_lib.${libSuffix}`;
const dylib = Deno.dlopen(libName, {
  js_to_rust_buffer: { parameters: ["pointer", "usize"], result: "void" },
  rust_to_js_buffer: {
    parameters: ["pointer"],
    result: "pointer"
  },
  eval_js: { parameters: ["pointer", "usize"], result: "void" }
});
const Rust = dylib.symbols;
const buffer = new TextEncoder().encode("Hello coming from Deno space");
const ret = dylib.symbols.rust_to_js_buffer(buffer);
console.log("dylib.symbols.rust_to_js_buffer(buffer):", ret);
export { Rust as default };
//# sourceMappingURL=rust.ffi.mjs.map
