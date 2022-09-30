/**
 * crypto
 * 用于为文件生成校验码
 */

import { createHash } from "crypto";
import { createReadStream } from "fs";
import { pipeline, Readable } from "stream";
import { promisify } from "util";
import { Buffer } from "../deps/deno.land/std@0.157.0/node/internal/buffer";

type algorithmType = "sha512" | "md5";
type Encoding = "base64" | "base64url" | "hex";

const pipelinePromise = promisify(pipeline);

export const checksumFile = (
  path: string,
  algorithm: algorithmType,
  encoding?: Encoding
) => checksum(createReadStream(path), algorithm, encoding);

export async function checksum(
  input: string | Buffer | Uint8Array | Readable,
  algorithm: string,
  encoding: Encoding = "hex"
): Promise<string> {
  const stream = toReadableStream(input);

  const hash = createHash(algorithm);
  await pipelinePromise(stream, hash);
  hash.end();
  return hash.digest(encoding) as string;
}

function isReadableStream(value: any): value is Readable {
  return (
    value != null &&
    typeof value === "object" &&
    typeof value.read === "function" &&
    typeof value.pipe === "function"
  );
}

const toReadableStream = (input: string | Buffer | Uint8Array | Readable) =>
  isReadableStream(input)
    ? input
    : new Readable({
        read() {
          this.push(input);
          this.push(null);
        },
      });
