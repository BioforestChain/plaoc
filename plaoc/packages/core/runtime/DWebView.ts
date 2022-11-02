import { callDeno } from "../deno/android.fn.ts";
// import { MetaData } from "@bfsx/metadata";
import { network } from "../deno/network.ts";

export class DWebView {
  entrys: string[];
  constructor(metaData: MetaData) {
    this.entrys = metaData.manifest.enters;
    this.initAppMetaData(metaData);
    network.dwebviewToDeno(); // 挂载轮询操作， 这里会自动处理来自前端的请求，并且处理操作返回到前端
  }

  /**
   * 初始化app元数据
   * @param metaData  元数据
   * @returns void
   */
  initAppMetaData(metaData: MetaData) {
    if (Object.keys(metaData).length === 0) return;
    network.syncCallDenoFunction(
      callDeno.initMetaData,
      `'${JSON.stringify(metaData)}'`
    );
  }
  /**
   * 激活DwebView
   * @param entry // DwebView入口
   */
  activity(entry: string) {
    // 判断在不在入口文件内
    if (this.entrys.toString().match(RegExp(`${entry}`))) {
      network.syncCallDenoFunction(callDeno.openDWebView, `"${entry}"`);
      return;
    }
    throw new Error("您传递的入口不在配置的入口内，需要在配置文件里配置入口");
  }
}
