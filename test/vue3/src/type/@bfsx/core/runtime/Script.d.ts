import { Runtime } from "../core";
export declare class ScriptModule {
    constructor(code: string, url: string);
}
/**
 * 将一个函数转换为 blob，然后为这个 blob 生成 URL 对象
 * @param module {add:()=>{},egg:()=>{}}
 * @returns URL
 */
export declare const fnTransformURL: (module: Runtime.TModule_fn) => string;
