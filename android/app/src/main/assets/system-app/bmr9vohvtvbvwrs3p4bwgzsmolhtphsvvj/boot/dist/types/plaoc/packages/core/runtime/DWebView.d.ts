import { MetaData } from "../../metadata/index.js";
export declare class DWebView {
    entrys: string[];
    constructor(metaData: MetaData);
    /**
     * 初始化app元数据
     * @param metaData  元数据
     * @returns void
     */
    initAppMetaData(metaData: MetaData): void;
    /**
     * 激活DwebView
     * @param entry // DwebView入口
     */
    activity(entry: string): void;
}
