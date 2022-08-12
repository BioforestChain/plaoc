/////////////////////////////
/// 这里封装调用deno的方法，然后暴露出去
/////////////////////////////
import Rust from "./rust.ffi";
const versionView = new Uint8Array(new ArrayBuffer(1));
const headView = new Uint8Array(new ArrayBuffer(2)); // 初始化头部标记
versionView[0] = 0x01; // 版本号都是1，表示消息
class Deno {
    /**
     * 创建消息头部
     */
    createHeader() {
        const tail = headView.length - 1;
        headView[tail] += 0x01; // 大端存储，先存内存高位
        // 如果末尾字节满了，左移一位，末尾清空
        if ((headView[tail] & 0xff) === 0xff) {
            this.bitLeftShifts();
            headView[tail] = 0x00;
        }
        console.log("headView =======>", Array.from(headView).map((n) => n.toString(2)));
    }
    /**
     * 调用deno的函数
     * @param handleFn
     * @param data
     */
    callFunction(handleFn, data) {
        const uint8Array = this.structureBinary(handleFn, data);
        Rust.send_buffer(uint8Array, uint8Array.length);
    }
    /** 针对64位
     * 第一块分区：版本号 2^8 8位，一个字节 1：表示消息，2：表示广播，4：心跳检测
     * 第二块分区：头部标记 2^16 16位 两个字节  根据版本号这里各有不同，假如是消息，就是0，1；如果是广播则是组
     * 第三块分区：数据主体 动态创建
     */
    structureBinary(fn, data = "") {
        const message = `{"function":["${fn}"],"data":${data}}`;
        // 字符 转 Uint8Array
        const encoder = new TextEncoder();
        const uint8Array = encoder.encode(message);
        return this.concatenate(versionView, headView, uint8Array);
    }
    /**
     * 左移一位，用来发消息定位
     */
    bitLeftShifts() {
        let rest = 0x00;
        for (let i = headView.length - 1; i >= 0; i--) {
            const v = headView[i];
            const newRest = (v & 0x80) > 0 ? 1 : 0; // 1000 0000
            headView[i] = ((v << 1) | rest) & 0xff; // 1111 1111
            rest = newRest;
        }
    }
    /**
     * 拼接Uint8Array
     * @param arrays Uint8Array[]
     * @returns Uint8Array
     */
    concatenate(...arrays) {
        let totalLength = 0;
        for (let arr of arrays) {
            totalLength += arr.length;
        }
        const result = new Uint8Array(totalLength);
        let offset = 0;
        for (let arr of arrays) {
            result.set(arr, offset);
            offset += arr.length;
        }
        return result;
    }
}
export const deno = new Deno();
