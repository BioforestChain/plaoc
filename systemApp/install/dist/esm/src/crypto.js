/**
 * crypto
 * 用于为文件生成校验码
 */
import { createHash } from "crypto";
import { createReadStream } from "fs";
import { pipeline, Readable } from "stream";
import { promisify } from "util";
const pipelinePromise = promisify(pipeline);
export const checksumFile = (path, algorithm, encoding) => checksum(createReadStream(path), algorithm, encoding);
export async function checksum(input, algorithm, encoding = "hex") {
    const stream = toReadableStream(input);
    const hash = createHash(algorithm);
    await pipelinePromise(stream, hash);
    hash.end();
    return hash.digest(encoding);
}
function isReadableStream(value) {
    return (value != null &&
        typeof value === "object" &&
        typeof value.read === "function" &&
        typeof value.pipe === "function");
}
const toReadableStream = (input) => isReadableStream(input)
    ? input
    : new Readable({
        read() {
            this.push(input);
            this.push(null);
        },
    });
