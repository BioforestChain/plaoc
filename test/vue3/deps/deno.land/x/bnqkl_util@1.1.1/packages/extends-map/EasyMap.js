export class EasyMap extends Map {
    // private _map: Map<F, V>;
    constructor(creater, entries, transformKey = (v) => v, _afterDelete) {
        super(entries);
        Object.defineProperty(this, "creater", {
            enumerable: true,
            configurable: true,
            writable: true,
            value: creater
        });
        Object.defineProperty(this, "transformKey", {
            enumerable: true,
            configurable: true,
            writable: true,
            value: transformKey
        });
        Object.defineProperty(this, "_afterDelete", {
            enumerable: true,
            configurable: true,
            writable: true,
            value: _afterDelete
        });
    }
    static from(args) {
        return new EasyMap(args.creater, args.entries, args.transformKey, args.afterDelete);
    }
    forceGet(key, creater = this.creater) {
        const k = this.transformKey(key);
        if (super.has(k)) {
            return super.get(k);
        }
        const res = creater(key, k);
        super.set(k, res);
        return res;
    }
    tryGet(key) {
        return this.get(this.transformKey(key));
    }
    trySet(key, val) {
        return this.set(this.transformKey(key), val);
    }
    tryDelete(key) {
        return this.delete(this.transformKey(key));
    }
    tryHas(key) {
        return this.has(this.transformKey(key));
    }
    delete(key) {
        const res = super.delete(key);
        if (res && this._afterDelete) {
            this._afterDelete(key);
        }
        return res;
    }
    get [Symbol.toStringTag]() {
        return "EasyMap";
    }
    static call(_this, creater, entries, transformKey, _afterDelete) {
        if (!(_this instanceof EasyMap)) {
            throw new TypeError("please use new keyword to create EasyMap instance.");
        }
        const protoMap = new EasyMap(creater, entries, transformKey, _afterDelete);
        const protoMap_PROTO = Object.getPrototypeOf(protoMap);
        const protoMap_PROTO_PROTO = Object.getPrototypeOf(protoMap_PROTO);
        const mapProps = Object.getOwnPropertyDescriptors(protoMap_PROTO_PROTO);
        for (const key in mapProps) {
            if (key !== "constructor") {
                const propDes = mapProps[key];
                if (typeof propDes.value === "function") {
                    propDes.value = propDes.value.bind(protoMap);
                }
                else {
                    if (typeof propDes.get === "function") {
                        propDes.get = propDes.get.bind(protoMap);
                    }
                    if (typeof propDes.set === "function") {
                        propDes.set = propDes.set.bind(protoMap);
                    }
                }
                Object.defineProperty(_this, key, propDes);
            }
        }
        const easymapProps = Object.getOwnPropertyDescriptors(protoMap_PROTO);
        for (const key in easymapProps) {
            if (key !== "constructor") {
                const propDes = easymapProps[key];
                if (typeof propDes.value === "function") {
                    propDes.value = propDes.value.bind(protoMap);
                }
                else {
                    if (typeof propDes.get === "function") {
                        propDes.get = propDes.get.bind(protoMap);
                    }
                    if (typeof propDes.set === "function") {
                        propDes.set = propDes.set.bind(protoMap);
                    }
                }
                Object.defineProperty(_this, key, propDes);
            }
        }
        const thisProps = Object.getOwnPropertyDescriptors(protoMap);
        for (const key in thisProps) {
            if (key !== "constructor")
                Object.defineProperty(_this, key, {
                    enumerable: true,
                    configurable: true,
                    get() {
                        return Reflect.get(protoMap, key);
                    },
                    set(v) {
                        Reflect.set(protoMap, key, v);
                    },
                });
        }
        return _this;
    }
}
