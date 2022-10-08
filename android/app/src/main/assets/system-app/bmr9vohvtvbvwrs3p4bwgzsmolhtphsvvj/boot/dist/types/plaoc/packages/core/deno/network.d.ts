declare class Network {
    private isWaitingData;
    private pool;
    /**反压高水位，暴露给开发者控制 */
    hightWaterMark: number;
    /**
   * 异步调用方法,这个是给后端调用的方法，不会传递数据到前端
   * @param handleFn
   * @param data
   * @returns
   */
    asyncCallDenoFunction(handleFn: string, data?: TNative, timeout?: number): Promise<string>;
    /**
  * 同步调用方法没返回值
  * @param handleFn
  * @param data
  */
    syncCallDenoFunction(handleFn: string, data?: string): void;
    /**
     * 轮询向rust拿数据，路径为：dwebView-js-(fetch)->kotlin-(ffi)->rust-(op)->deno-js->kotlin(eventJs)->dwebView-js
     * 这里是接收dwebView-js操作系统API转发到后端的请求
    */
    waterOverflow(): Promise<void>;
    /**
     * 操作传递到kotlin
     * @param handler
     * @returns
     */
    callKotlinFactory(handler: {
        function: string;
        data: string;
        channelId: string;
    }): void;
    /**
     * 数据传递到DwebView
     * @param data
     * @returns
     */
    callDwebViewFactory(data: string): void;
    /**
    * 传递消息给DwebView-js,路径为：deno-js-(op)->rust-(ffi)->kotlin-(evaljs)->dwebView-js
    * @param wb
    * @param data
    * @returns
    */
    handlerEvalJs(wb: string, data: string): void;
}
declare type TNative = boolean | object | string | number;
export declare const network: Network;
export {};
