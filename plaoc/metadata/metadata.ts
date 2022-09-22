import { IManifest, IDwebview, IMetaData, IImportMap } from "./metadataType";
class MetaData implements IMetaData {
  manifest: Manifest;
  dwebview: DWebView;
  whitelist?: string[];
  constructor(metaData: IMetaData) {
    this.manifest = metaData.manifest;
    this.dwebview = metaData.dwebview;
    this.whitelist = metaData.whitelist;
  }
}

class Manifest implements IManifest {
  origin!: string;
  author!: string[];
  description!: string;
  keywords!: string[];
  dwebId!: string;
  privateKey!: string;
  enters!: string[];
  // constructor(meta: IManifest) {
  //   this.origin = meta.origin;
  //   this.author = meta.author;
  //   this.description = meta.description;
  //   this.keywords = meta.keywords;
  //   this.dwebId = meta.dwebId;
  //   this.privateKey = meta.privateKey;
  //   this.enter = meta.enter;
  // }
}

class DWebView implements IDwebview {
  importmap!: ImportMap[];
}

class ImportMap implements IImportMap {
  url!: string;
  response!: string;
}

export { MetaData };
