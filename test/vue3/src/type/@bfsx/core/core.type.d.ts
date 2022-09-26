export declare namespace Runtime {
    type DwebViewId = string;
    interface IManifestApp {
        id: string;
        name: string;
        versionCode: number;
        minBfsVersionCode: number;
        defaultEntry: string;
        entryResourceMap?: Map<string, string>;
    }
    type TModule_fn = {
        [fn: string]: Function;
    };
}
