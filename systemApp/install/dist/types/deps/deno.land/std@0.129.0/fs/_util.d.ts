/**
 * Test whether or not `dest` is a sub-directory of `src`
 * @param src src file path
 * @param dest dest file path
 * @param sep path separator
 */
export declare function isSubdir(src: string, dest: string, sep?: string): boolean;
export declare type PathType = "file" | "dir" | "symlink";
/**
 * Get a human readable file type string.
 *
 * @param fileInfo A FileInfo describes a file and is returned by `stat`,
 *                 `lstat`
 */
export declare function getFileInfoType(fileInfo: Deno.FileInfo): PathType | undefined;
