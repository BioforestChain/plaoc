declare class Deno {
    /**
     * 创建消息头部
     */
    createHeader(): void;
    /**
     * 调用deno的函数
     * @param handleFn
     * @param data
     */
    callFunction(handleFn: string, data?: string): void;
    /** 针对64位
     * 第一块分区：版本号 2^8 8位，一个字节 1：表示消息，2：表示广播，4：心跳检测
     * 第二块分区：头部标记 2^16 16位 两个字节  根据版本号这里各有不同，假如是消息，就是0，1；如果是广播则是组
     * 第三块分区：数据主体 动态创建
     */
    structureBinary(fn: string, data?: string): Uint8Array;
    /**
     * 左移一位，用来发消息定位
     */
    bitLeftShifts(): void;
    /**
     * 拼接Uint8Array
     * @param arrays Uint8Array[]
     * @returns Uint8Array
     */
    concatenate(...arrays: Uint8Array[]): Uint8Array;
}
export declare const deno: Deno;
export {};
