import type { compressInterface, uncompressInterface } from "../interface.js";
export declare function uncompress(src: string, dest: string, options?: uncompressInterface): Promise<void>;
export declare function compress(src: string, dest: string, options?: compressInterface): Promise<void>;
