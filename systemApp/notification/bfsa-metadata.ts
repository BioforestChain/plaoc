import { metaConfig } from "@bfsx/metadata";

export default metaConfig({
  manifest: {
    version: "0.0.1",
    name: "消息中心",
    icon: "",
    engines: {
      dwebview: "~1.0.0",
    },
    // 应用所属链的名称（系统应用的链名为通配符“*”，其合法性由节点程序自身决定，不跟随链上数据）
    origin: "bfchain",
    // 开发者
    author: [""],
    // 应用搜索的描述
    description: "",
    // 应用搜索的关键字
    keywords: ["notification", "message"],
    // 私钥文件，用于最终的应用签名
    privateKey: "bmr9vohvtvbvwrs3p4bwgzsmolhtphsvvj",
    homepage: "docs.plaoc.com",
    maxAge: 1,
    // 应用入口，可以配置多个，其中index为缺省名称。
    // 外部可以使用 DWEB_ID.bfchain (等价同于index.DWEB_ID.bfchain)、admin.DWEB_ID.bfchain 来启动其它页面
    enters: [""],
    //本次发布的信息，一般存放更新信息
    releaseNotes: "消息中心初始化",
    //  本次发布的标题，用于展示更新信息时的标题
    releaseName: "消息中心0.0.1",
    // 发布日期
    releaseDate: "2022-10-18 17:00:00",
  },
  whitelist: [],
  dwebview: {
    importmap: [],
  },
});
