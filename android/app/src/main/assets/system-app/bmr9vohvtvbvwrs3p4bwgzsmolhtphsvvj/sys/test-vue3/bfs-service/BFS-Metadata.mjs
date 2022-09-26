const metaData = {
  manifest: {
    origin: "bfchain",
    author: ["waterbang,water_bang@163.com"],
    description: "Awasome DWeb",
    keywords: ["demo"],
    dwebId: "bmr9vohvtvbvwrs3p4bwgzsmolhtphsvvj",
    privateKey: "bmr9vohvtvbvwrs3p4bwgzsmolhtphsvvj",
    enters: ["index.html"]
  },
  whitelist: ["https://unpkg.com", "https://cn.vitejs.dev"],
  dwebview: {
    importmap: [
      {
        url: "/getBlockInfo",
        response: "https://62b94efd41bf319d22797acd.mockapi.io/bfchain/v1/getBlockInfo"
      },
      {
        url: "/getBlockHigh",
        response: "https://62b94efd41bf319d22797acd.mockapi.io/bfchain/v1/getBlockInfo"
      },
      {
        url: "/app/bfchain.dev/index.html",
        response: "/app/bfchain.dev/index.html"
      },
      {
        url: "/api/*",
        response: "./api/*"
      },
      {
        url: "/api/upload",
        response: "/api/update"
      }
    ]
  }
};
export {
  metaData
};
//# sourceMappingURL=BFS-Metadata.mjs.map
