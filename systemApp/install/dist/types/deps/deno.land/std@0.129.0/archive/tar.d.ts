declare type Reader = Deno.Reader;
interface TarHeader {
    [key: string]: Uint8Array;
}
export interface TarData {
    fileName?: string;
    fileNamePrefix?: string;
    fileMode?: string;
    uid?: string;
    gid?: string;
    fileSize?: string;
    mtime?: string;
    checksum?: string;
    type?: string;
    ustar?: string;
    owner?: string;
    group?: string;
}
export interface TarDataWithSource extends TarData {
    /**
     * file to read
     */
    filePath?: string;
    /**
     * buffer to read
     */
    reader?: Reader;
}
export interface TarInfo {
    fileMode?: number;
    mtime?: number;
    uid?: number;
    gid?: number;
    owner?: string;
    group?: string;
    type?: string;
}
export interface TarOptions extends TarInfo {
    /**
     * append file
     */
    filePath?: string;
    /**
     * append any arbitrary content
     */
    reader?: Reader;
    /**
     * size of the content to be appended
     */
    contentSize?: number;
}
export interface TarMeta extends TarInfo {
    fileName: string;
    fileSize?: number;
}
interface TarEntry extends TarMeta {
}
/**
 * A class to create a tar archive
 */
export declare class Tar {
    data: TarDataWithSource[];
    constructor();
    /**
     * Append a file to this tar archive
     * @param fn file name
     *                 e.g., test.txt; use slash for directory separators
     * @param opts options
     */
    append(fn: string, opts: TarOptions): Promise<void>;
    /**
     * Get a Reader instance for this tar data
     */
    getReader(): Reader;
}
declare class TarEntry implements Reader {
    #private;
    constructor(meta: TarMeta, header: TarHeader, reader: Reader | (Reader & Deno.Seeker));
    get consumed(): boolean;
    read(p: Uint8Array): Promise<number | null>;
    discard(): Promise<void>;
}
/**
 * A class to extract a tar archive
 */
export declare class Untar {
    #private;
    reader: Reader;
    block: Uint8Array;
    constructor(reader: Reader);
    extract(): Promise<TarEntry | null>;
    [Symbol.asyncIterator](): AsyncIterableIterator<TarEntry>;
}
export {};
