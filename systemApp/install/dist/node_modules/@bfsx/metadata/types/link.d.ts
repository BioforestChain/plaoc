export declare class LinkMetadata {
    version: string;
    bfsAppId: string;
    name: string;
    icon: string;
    author: string[];
    autoUpdate: AutoUpdate;
}
export declare class AutoUpdate {
    maxAge: number;
    provider: number;
    url: string;
    version: string;
    files: Files[];
    releaseNotes: string;
    releaseName: string;
    releaseDate: string;
}
export declare class Files {
    url: string;
    size: number;
    sha512: string;
}
