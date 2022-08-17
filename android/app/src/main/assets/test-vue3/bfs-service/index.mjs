import { MetaData } from "../../plaoc/metadata/dist/esm/metadata.mjs";
import { metaData } from "./BFS-Metadata.mjs";
import { DWebView } from "../../plaoc/core/dist/esm/runtime/DWebView.mjs";
import "../../plaoc/core/dist/esm/deno/index.mjs";
const webView = new DWebView(new MetaData(metaData));
webView.activity();
//# sourceMappingURL=index.mjs.map
