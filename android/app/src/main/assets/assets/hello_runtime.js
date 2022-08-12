// Copyright 2018-2022 the Deno authors. All rights reserved. MIT license.
console.log("Hello world!2222!!!");
console.log("Deno Apis: %o", Object.keys(Deno));
const a = await import("./2.js")
console.log(a)
const w = new Worker(new URL("./worker.js", import.meta.url), {
    type: "module",
    deno: {
        namespace: true,
    },
});
console.log(w);