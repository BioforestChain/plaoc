import { DWebView } from "./runtime/DWebView.mjs";
import "./deno/index.mjs";
const dwebView = async (metaData) => {
  const dwebview = new DWebView(metaData);
  dwebview.activity();
  for await (let num of dwebview.waterOverflow()) {
    if (!num.done) {
      console.log("webView.waterOverflow:", num);
    }
  }
  return dwebview;
};
export { dwebView };
//# sourceMappingURL=index.mjs.map
