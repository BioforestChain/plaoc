export class MetaData {
    constructor(metaData) {
        Object.defineProperty(this, "manifest", {
            enumerable: true,
            configurable: true,
            writable: true,
            value: void 0
        });
        Object.defineProperty(this, "dwebview", {
            enumerable: true,
            configurable: true,
            writable: true,
            value: void 0
        });
        Object.defineProperty(this, "whitelist", {
            enumerable: true,
            configurable: true,
            writable: true,
            value: void 0
        });
        this.manifest = metaData.manifest;
        this.dwebview = metaData.dwebview;
        this.whitelist = metaData.whitelist;
    }
}
export class Manifest {
    constructor() {
        Object.defineProperty(this, "version", {
            enumerable: true,
            configurable: true,
            writable: true,
            value: void 0
        });
        Object.defineProperty(this, "name", {
            enumerable: true,
            configurable: true,
            writable: true,
            value: void 0
        });
        Object.defineProperty(this, "icon", {
            enumerable: true,
            configurable: true,
            writable: true,
            value: void 0
        });
        Object.defineProperty(this, "engines", {
            enumerable: true,
            configurable: true,
            writable: true,
            value: void 0
        });
        Object.defineProperty(this, "origin", {
            enumerable: true,
            configurable: true,
            writable: true,
            value: void 0
        });
        Object.defineProperty(this, "author", {
            enumerable: true,
            configurable: true,
            writable: true,
            value: void 0
        });
        Object.defineProperty(this, "description", {
            enumerable: true,
            configurable: true,
            writable: true,
            value: void 0
        });
        Object.defineProperty(this, "keywords", {
            enumerable: true,
            configurable: true,
            writable: true,
            value: void 0
        });
        Object.defineProperty(this, "privateKey", {
            enumerable: true,
            configurable: true,
            writable: true,
            value: void 0
        });
        Object.defineProperty(this, "homepage", {
            enumerable: true,
            configurable: true,
            writable: true,
            value: void 0
        });
        // 应用最大缓存时间
        Object.defineProperty(this, "maxAge", {
            enumerable: true,
            configurable: true,
            writable: true,
            value: void 0
        });
        // 后端入口地址，开发者不用管，打包的时候会打包写到bfsa-metadata.json
        Object.defineProperty(this, "bfsaEntry", {
            enumerable: true,
            configurable: true,
            writable: true,
            value: void 0
        });
        Object.defineProperty(this, "enters", {
            enumerable: true,
            configurable: true,
            writable: true,
            value: void 0
        });
        //本次发布的信息，一般存放更新信息
        Object.defineProperty(this, "releaseNotes", {
            enumerable: true,
            configurable: true,
            writable: true,
            value: void 0
        });
        //  本次发布的标题，用于展示更新信息时的标题
        Object.defineProperty(this, "releaseName", {
            enumerable: true,
            configurable: true,
            writable: true,
            value: void 0
        });
        // 发布日期
        Object.defineProperty(this, "releaseDate", {
            enumerable: true,
            configurable: true,
            writable: true,
            value: void 0
        });
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
}
export class DWebView {
    constructor() {
        Object.defineProperty(this, "importmap", {
            enumerable: true,
            configurable: true,
            writable: true,
            value: void 0
        });
    }
}
export class ImportMap {
    constructor() {
        Object.defineProperty(this, "url", {
            enumerable: true,
            configurable: true,
            writable: true,
            value: void 0
        });
        Object.defineProperty(this, "response", {
            enumerable: true,
            configurable: true,
            writable: true,
            value: void 0
        });
    }
}
export function metaConfig(metaData) {
    return new MetaData(metaData);
}
