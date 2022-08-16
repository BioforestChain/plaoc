import { MetaData } from "../../../android-deno-runtime-example/plaoc/metadata/dist/esm/index.mjs";
import { metaData } from "./BFS-Metadata.mjs";
import { DWebView } from "../../../android-deno-runtime-example/plaoc/core/dist/esm/runtime/DWebView.mjs";
import "../../../android-deno-runtime-example/plaoc/core/dist/esm/deno/index.mjs";
console.log("qaqqqqqq")
const webView = new DWebView(new MetaData(metaData));
console.log("qaqqqqq222q")
webView.activity();
console.log("333")
//# sourceMappingURL=index.mjs.map
