import { Buffer } from "./buffer.js";
/** Reader utility for strings */
export declare class StringReader extends Buffer {
    constructor(s: string);
}
/** Reader utility for combining multiple readers */
export declare class MultiReader implements Deno.Reader {
    private readonly readers;
    private currentIndex;
    constructor(...readers: Deno.Reader[]);
    read(p: Uint8Array): Promise<number | null>;
}
/**
 * A `LimitedReader` reads from `reader` but limits the amount of data returned to just `limit` bytes.
 * Each call to `read` updates `limit` to reflect the new amount remaining.
 * `read` returns `null` when `limit` <= `0` or
 * when the underlying `reader` returns `null`.
 */
export declare class LimitedReader implements Deno.Reader {
    reader: Deno.Reader;
    limit: number;
    constructor(reader: Deno.Reader, limit: number);
    read(p: Uint8Array): Promise<number | null>;
}
