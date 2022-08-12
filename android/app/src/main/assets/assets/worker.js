console.log("worker import.meta.url: %s", import.meta.url); // worker import.meta.url: file:///assets/worker.js
console.log("Deno Apis: %o", Object.keys(Deno));
// setInterval(()=>{
//     console.log('[%s] worker time: %s', import.meta.url, new Date())
// },10000);


console.log("global Apis: %o", Object.keys(globalThis));
// global Apis: [
//     "Deno", "queueMicrotask",
//     "dispatchEvent", "addEventListener",
//     "removeEventListener", "reportError",
//     "AbortSignal", "AbortController",
//     "atob", "btoa",
//     "clearInterval", "clearTimeout",
//     "crypto", "fetch",
//     "performance", "setInterval",
//     "setTimeout", "structuredClone",
//     "location", "navigator",
//     "self", "postMessage",
//     "name", "onmessage",
//     "onerror"
// ]


// D / myrust:: BFS: deno_runtime:: web_worker: received worker module evaluate Ok(
//     Ok(
//         (),
//     ),
// )
// D / ProfileInstaller: Installing profile for org.bfchain.rust.example
// I / myrust:: BFS: deno_core:: ops_builtin: [file:///assets/2.js] worker time: Tue Jul 05 2022 17:38:10 GMT+0800 (China Standard Time)


// Deno Apis: [
//     "core", "internal", "resources",
//     "close", "metrics", "test",
//     "bench", "Process", "run",
//     "isatty", "writeFileSync", "writeFile",
//     "writeTextFileSync", "writeTextFile", "readTextFile",
//     "readTextFileSync", "readFile", "readFileSync",
//     "watchFs", "chmodSync", "chmod",
//     "chown", "chownSync", "copyFileSync",
//     "cwd", "makeTempDirSync", "makeTempDir",
//     "makeTempFileSync", "makeTempFile", "memoryUsage",
//     "mkdirSync", "mkdir", "chdir",
//     "copyFile", "readDirSync", "readDir",
//     "readLinkSync", "readLink", "realPathSync",
//     "realPath", "removeSync", "remove",
//     "renameSync", "rename", "version",
//     "build", "statSync", "lstatSync",
//     "stat", "lstat", "truncateSync",
//     "truncate", "ftruncateSync", "ftruncate",
//     "errors", "customInspect", "inspect",
//     "env", "exit", "execPath",
//     "Buffer", "readAll", "readAllSync",
//     "writeAll", "writeAllSync", "copy",
//     "iter", "iterSync", "SeekMode",
//     "read", "readSync", "write",
//     "writeSync", "File", "FsFile",
//     "open", "openSync", "create",
//     "createSync", "stdin", "stdout",
//     "stderr", "seek", "seekSync",
//     "connect", "listen", "connectTls",
//     "listenTls", "startTls", "shutdown",
//     "fstatSync", "fstat", "fsyncSync",
//     "fsync", "fdatasyncSync", "fdatasync",
//     "symlink", "symlinkSync", "link",
//     "linkSync",
//     ... 48 more items
// ]
