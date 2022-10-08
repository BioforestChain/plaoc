import { IDwebview, IImportMap, IManifest, IMetaData } from "./metadataType.js";
export declare class MetaData implements IMetaData {
    manifest: Manifest;
    dwebview: DWebView;
    whitelist?: string[];
    constructor(metaData: IMetaData);
}
export declare class Manifest implements IManifest {
    version: string;
    name: string;
    icon: string;
    engines: {
        dwebview: string;
    };
    origin: string;
    author: string[];
    description: string;
    keywords: string[];
    privateKey: string;
    homepage: string;
    maxAge: number;
    bfsaEntry?: string;
    enters: string[];
    releaseNotes: string;
    releaseName: string;
    releaseDate: string;
}
export declare class DWebView implements IDwebview {
    importmap: ImportMap[];
}
export declare class ImportMap implements IImportMap {
    url: string;
    response: string;
}
export declare function metaConfig(metaData: MetaData): MetaData;
