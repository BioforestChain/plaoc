import { ls, EFilterType } from "@bfsx/vfs"

try {
  const fs = ls("/", {
    filter: [{
      type: EFilterType.file,
      name: ["*"]
    }],
    recursive: true
  })
  console.log("vfs 哈哈哈: ", fs)

} catch (e) {
  console.log(e)
}
