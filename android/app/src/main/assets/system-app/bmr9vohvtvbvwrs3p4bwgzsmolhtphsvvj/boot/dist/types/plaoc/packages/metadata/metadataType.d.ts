export interface IMetaData {
    manifest: IManifest;
    dwebview: IDwebview;
    whitelist?: string[];
}
export interface IManifest {
    version: string;
    name: string;
    icon: string;
    engines: {
        dwebview: string;
    };
    origin: string;
    author: string[];
    description: string;
    maxAge: number;
    bfsaEntry?: string;
    keywords: string[];
    homepage: string;
    privateKey: string;
    enters: string[];
    releaseNotes: string;
    releaseName: string;
    releaseDate: string;
}
export interface IDwebview {
    importmap: IImportMap[];
}
export interface IImportMap {
    url: string;
    response: string;
}
