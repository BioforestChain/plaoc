// Copyright 2018-2022 the Deno authors. All rights reserved. MIT license.
console.log("Hello world!");
// console.log(Deno);
function rustCallback(data) {
  console.log("接收到rust的消息", data)
}
