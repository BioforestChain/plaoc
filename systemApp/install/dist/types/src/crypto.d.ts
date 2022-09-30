/**
 * crypto
 * 用于为文件生成校验码
 */
/// <reference types="node" />
import { Readable } from "stream";
import { Buffer } from "../deps/deno.land/std@0.157.0/node/internal/buffer";
declare type algorithmType = "sha512" | "md5";
declare type Encoding = "base64" | "base64url" | "hex";
export declare const checksumFile: (path: string, algorithm: algorithmType, encoding?: Encoding) => Promise<string>;
export declare function checksum(input: string | Buffer | Uint8Array | Readable, algorithm: string, encoding?: Encoding): Promise<string>;
export {};
