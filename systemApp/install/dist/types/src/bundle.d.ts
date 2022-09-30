import "@bfsx/typings";
/**
 * 打包入口
 * @param options
 */
export declare function bundle(options: {
    bfsAppId: string;
    frontPath: string;
    backPath: string;
}): Promise<void>;
