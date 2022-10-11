import { ls, EFilterType } from "@bfsx/vfs"

try {
  const fs = await ls("/", {
    filter: {
      type: EFilterType.file,
      name: ["*\.ts)"]
    },
    recursive: true
  })
  console.log("vfs 哈哈哈: ", fs)

} catch (e) {
  console.log(e)
}
