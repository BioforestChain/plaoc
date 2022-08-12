const metaData = {
  baseUrl: "",
  manifest: {
    origin: "bfchain",
    author: ["waterbang,water_bang@163.com"],
    description: "Awasome DWeb",
    keywords: ["demo"],
    dwebId: "bmr9vohvtvbvwrs3p4bwgzsmolhtphsvvj",
    privateKey: "bmr9vohvtvbvwrs3p4bwgzsmolhtphsvvj",
    enter: "index.html"
  },
  whitelist: ["https://unpkg.com"],
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
        url: "/index.html",
        response: "/index.html"
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
        url: "/api-source/*",
        response: "./api/*"
      }
    ]
  }
};
export { metaData };
//# sourceMappingURL=BFS-Metadata.mjs.map
