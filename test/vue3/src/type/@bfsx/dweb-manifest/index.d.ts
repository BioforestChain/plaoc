declare module "@bfsx/dweb-manifest" {
  export type { IMetaData, Manifest, router };
  export declare class MetaData {
    manifest: Manifest;
    router: router[];
    whitelist?: string[];
    baseUrl?: string;
    constructor(metaData: IMetaData);
  }

  export interface IMetaData {
    manifest: Manifest;
    router: router[];
    whitelist: string[];
    baseUrl: string;
  }
  export interface Manifest {
    origin: string;
    author: string[];
    description: string;
    keywords: string[];
    dwebId: string;
    privateKey: string;
    enter: string;
  }
  export interface router {
    url: string;
    header: {
      method?: string;
      contentType?: string;
      response: string;
      StatusCode?: number;
    };
  }
}
