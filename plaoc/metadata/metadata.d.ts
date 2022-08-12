export interface IMetaData {
  manifest: IManifest;
  dwebview: IDwebview;
  whitelist?: string[];
  baseUrl: string;
}
export interface IManifest {
  // 应用所属链的名称（系统应用的链名为通配符“*”，其合法性由节点程序自身决定，不跟随链上数据）
  origin: string;
  // 开发者
  author: string[];
  // 应用搜索的描述
  description: string;
  // 应用搜索的关键字
  keywords: string[];
  // 应用ID，参考共识标准
  dwebId: string;
  // 私钥文件，用于最终的应用签名
  privateKey: string;
  // 应用入口，可以配置多个，其中index为缺省名称。
  // 外部可以使用 DWEB_ID.bfchain (等价同于index.DWEB_ID.bfchain)、admin.DWEB_ID.bfchain 来启动其它页面
  enter: string;
}

export interface IDwebview {
  importmap: Importmap[];
}

export interface IImportMap {
  url: string;
  response: string;
}
