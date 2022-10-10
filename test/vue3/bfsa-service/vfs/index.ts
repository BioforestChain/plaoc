import { ls, EFilterType } from "@bfsx/vfs"

console.log("----------------start test vfs ls--------------------")
console.log("vfs 哈哈哈: ", ls("/", {
  filter: [{
    type: EFilterType.file,
    name: ["*"]
  }],
  recursive: true
}))
console.log("-----------------end test vfs ls---------------------")
