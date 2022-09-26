import { MetaData } from "@bfsx/dweb-manifest";
export declare class DWebView {
    url: string;
    constructor(metaData: MetaData);
    initAppMetaData(metaData: MetaData): void;
    onRequest(url: string): Promise<string>;
    activity(entry: string): void;
}
