export function crc32(arr) {
    if (typeof arr === "string") {
        arr = new TextEncoder().encode(arr);
    }
    let crc = -1, i, j, l, temp, poly = 0xEDB88320;
    for (i = 0, l = arr.length; i < l; i += 1) {
        temp = (crc ^ arr[i]) & 0xff;
        for (j = 0; j < 8; j += 1) {
            if ((temp & 1) === 1) {
                temp = (temp >>> 1) ^ poly;
            }
            else {
                temp = (temp >>> 1);
            }
        }
        crc = (crc >>> 8) ^ temp;
    }
    return numberToHex(crc ^ -1);
}
export class Crc32Stream {
    bytes = [];
    poly = 0xEDB88320;
    crc = 0 ^ -1;
    encoder = new TextEncoder();
    #crc32 = "";
    constructor() {
        this.reset();
    }
    get crc32() {
        return this.#crc32;
    }
    reset() {
        this.#crc32 = "";
        this.crc = 0 ^ -1;
        for (let n = 0; n < 256; n += 1) {
            let c = n;
            for (let k = 0; k < 8; k += 1) {
                if (c & 1) {
                    c = this.poly ^ (c >>> 1);
                }
                else {
                    c = c >>> 1;
                }
            }
            this.bytes[n] = c >>> 0;
        }
    }
    append(arr) {
        if (typeof arr === "string") {
            arr = this.encoder.encode(arr);
        }
        let crc = this.crc;
        for (let i = 0, l = arr.length; i < l; i += 1) {
            crc = (crc >>> 8) ^ this.bytes[(crc ^ arr[i]) & 0xff];
        }
        this.crc = crc;
        this.#crc32 = numberToHex(crc ^ -1);
        return this.#crc32;
    }
}
export function numberToHex(n) {
    return (n >>> 0).toString(16);
}
export function hexToUint8Array(str) {
    if (str.length === 0 || str.length % 2 !== 0) {
        throw new Error(`The string "${str}" is not valid hex.`);
    }
    return new Uint8Array(str.match(/.{1,2}/g).map((byte) => parseInt(byte, 16)));
}
export function uint8ArrayToHex(bytes) {
    return bytes.reduce((str, byte) => str + byte.toString(16).padStart(2, "0"), "");
}
