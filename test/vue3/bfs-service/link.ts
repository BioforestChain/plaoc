import { LinkMetadata } from "@bfsx/metadata";
const link: LinkMetadata = {
  version: "",
  bfsAppId: "",
  name: "",
  icon: "",
  author: [],
  autoUpdate: {
    maxAge: 3,
    provider: 1,
    url: "bfs-app-minzt/appversion.json",
    version: "1.0.1",
    files: [{
      url: "https://www.google.cn",
      size: 1024,
      sha512: "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
    }],
    releaseNotes: "本次发布的信息，一般存放更新信息",
    releaseName: "本次发布的标题，用于展示更新信息时的标题",
    releaseDate: "2022-08-26"
  }
}
