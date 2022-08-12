class MetaData {
  constructor(metaData) {
    this.manifest = metaData.manifest;
    this.dwebview = metaData.dwebview;
    this.whitelist = metaData.whitelist;
    this.baseUrl = `https://${metaData.manifest.dwebId}.dweb`;
  }
}
export { MetaData };
//# sourceMappingURL=index.mjs.map
