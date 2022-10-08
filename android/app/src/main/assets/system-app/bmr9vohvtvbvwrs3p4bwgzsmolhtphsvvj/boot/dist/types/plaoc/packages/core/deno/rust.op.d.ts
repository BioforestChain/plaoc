import "../../typings/index.js";
/**js 到rust的消息 */
export declare function js_to_rust_buffer(data: Uint8Array): void;
/**js 到rust的消息： 调用android方法执行evenjs，即传递消息给前端 */
export declare function eval_js(data: Uint8Array): void;
/**循环从rust里拿数据 */
export declare function loopRustBuffer(): {
    next(): Promise<{
        value: string;
        done: boolean;
    }>;
    return(): void;
    throw(): void;
};
