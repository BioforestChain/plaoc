import { MetaData } from "@bfsx/metadata";
// import "./node_modules/index.html";

export const metaData: MetaData = {
  baseUrl: "",
  manifest: {
    // 应用所属链的名称（系统应用的链名为通配符“*”，其合法性由节点程序自身决定，不跟随链上数据）
    origin: "bfchain",
    // 开发者
    author: ["waterbang,water_bang@163.com"],
    // 应用搜索的描述
    description: "Awasome DWeb",
    // 应用搜索的关键字
    keywords: ["demo"],
    // 应用ID，参考共识标准
    dwebId: "bmr9vohvtvbvwrs3p4bwgzsmolhtphsvvj",
    // 私钥文件，用于最终的应用签名
    privateKey: "bmr9vohvtvbvwrs3p4bwgzsmolhtphsvvj",
    // 应用入口，可以配置多个，其中index为缺省名称。
    // 外部可以使用 DWEB_ID.bfchain (等价同于index.DWEB_ID.bfchain)、admin.DWEB_ID.bfchain 来启动其它页面
    enter: "index.html",
  },
  //  这里配置的白名单将不被拦截
  whitelist: ["https://unpkg.com"],
  // 定义路由，这里与enter是完全独立的存在。
  // 外部可以使用 admin.DWEB_ID.bfchain/routeA 来传入参数
  dwebview: {
    importmap: [
      {
        url: "/getBlockInfo",
        response:
          "https://62b94efd41bf319d22797acd.mockapi.io/bfchain/v1/getBlockInfo",
      },
      {
        url: "/getBlockHigh",
        response:
          "https://62b94efd41bf319d22797acd.mockapi.io/bfchain/v1/getBlockInfo",
      },
      {
        url: "/index.html",
        response: "/index.html",
      },
      {
        url: "/app/bfchain.dev/index.html",
        response: "/app/bfchain.dev/index.html",
      },
      {
        url: "/api/*",
        response: "./api/*",
      },
      {
        url: "/api-source/*",
        response: "./api/*",
      },
    ],
  },
};

// // web
// await fetch("./api/z.ts?a=1");

// // z.bfsapi.ts
// export default (req, res) => {
//   return { echo: queryData };
// };

// dwebview -> nodejs

// importMap assert

/// dwebview index.ts
