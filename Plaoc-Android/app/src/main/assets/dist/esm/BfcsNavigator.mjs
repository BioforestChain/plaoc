var __accessCheck = (obj, member, msg) => {
  if (!member.has(obj))
    throw TypeError("Cannot " + msg);
};
var __privateGet = (obj, member, getter) => {
  __accessCheck(obj, member, "read from private field");
  return getter ? getter.call(obj) : member.get(obj);
};
var __privateAdd = (obj, member, value) => {
  if (member.has(obj))
    throw TypeError("Cannot add the same private member more than once");
  member instanceof WeakSet ? member.add(obj) : member.set(obj, value);
};
var _routes, _onPushEvt, _onPopEvt, _onReplaceEvt, _ownNavs, _onForkEvt, _onActivicedEvt, _onDestroyEvt;
import { EvtOut } from "./EvtOut.mjs";
const _BfcsNavigator = class {
  constructor(info, parentInfo, _ffi) {
    __privateAdd(this, _routes, []);
    __privateAdd(this, _onPushEvt, new EvtOut());
    __privateAdd(this, _onPopEvt, new EvtOut());
    __privateAdd(this, _onReplaceEvt, new EvtOut());
    __privateAdd(this, _ownNavs, /* @__PURE__ */ new WeakSet());
    __privateAdd(this, _onForkEvt, new EvtOut());
    __privateAdd(this, _onActivicedEvt, new EvtOut());
    __privateAdd(this, _onDestroyEvt, new EvtOut());
    this.info = info;
    this.parentInfo = parentInfo;
    this._ffi = _ffi;
  }
  get length() {
    return __privateGet(this, _routes).length;
  }
  at(index) {
    return __privateGet(this, _routes)[index < 0 ? __privateGet(this, _routes).length + index : index];
  }
  push(route) {
    const success = this._ffi.push(this.info.nid, route);
    if (success) {
      __privateGet(this, _routes).push(route);
      __privateGet(this, _onPushEvt).emit({ route });
    }
    return success;
  }
  get onPush() {
    return __privateGet(this, _onPushEvt).toAsyncGenerator();
  }
  pop(count = 1) {
    const result = this._ffi.pop(this.info.nid, count);
    if (result > 0) {
      for (const route of __privateGet(this, _routes).splice(-result)) {
        __privateGet(this, _onPopEvt).emit({ route });
      }
    }
    return result;
  }
  get onPop() {
    return __privateGet(this, _onPopEvt).toAsyncGenerator();
  }
  replace(route, at = -1) {
    const index = at < 0 ? __privateGet(this, _routes).length + at : at;
    if (index in __privateGet(this, _routes) === false) {
      return false;
    }
    const success = this._ffi.replace(this.info.nid, index, route);
    if (success) {
      const removedRoutes = __privateGet(this, _routes).splice(at, 1, route);
      __privateGet(this, _onReplaceEvt).emit({ newRoute: route, oldRoute: removedRoutes[0] });
    }
    return success;
  }
  get onReplace() {
    return __privateGet(this, _onReplaceEvt).toAsyncGenerator();
  }
  fork(opts) {
    const parentNav = opts.fromNavigator === null ? null : opts.fromNavigator || this;
    const parentNid = parentNav?.info.nid ?? -1;
    const result = this._ffi.fork(this.info.nid, opts.data, parentNid);
    if (result >= 0) {
      const newNavigator = new _BfcsNavigator(Object.freeze({ data: opts.data, nid: result }), parentNav?.info, this._ffi);
      __privateGet(this, _ownNavs).add(newNavigator);
      __privateGet(this, _onForkEvt).emit({
        newNavigator,
        fromNavigator: parentNav
      });
      return newNavigator;
    }
  }
  get onFork() {
    return __privateGet(this, _onForkEvt).toAsyncGenerator();
  }
  checkout(navigator) {
    if (__privateGet(this, _ownNavs).has(navigator) === false) {
      return false;
    }
    return this._ffi.checkout(this.info.nid, navigator.info.nid);
  }
  get onActiviced() {
    return __privateGet(this, _onActivicedEvt).toAsyncGenerator();
  }
  destroy(navigator, reason) {
    if (__privateGet(this, _ownNavs).has(navigator) === false) {
      return false;
    }
    const success = this._ffi.destroy(this.info.nid, navigator.info.nid);
    if (success) {
      __privateGet(this, _ownNavs).delete(navigator);
      __privateGet(navigator, _onDestroyEvt).emit({ reason });
    }
    return success;
  }
  get onDestroy() {
    return __privateGet(this, _onDestroyEvt).toAsyncGenerator();
  }
};
let BfcsNavigator = _BfcsNavigator;
_routes = new WeakMap();
_onPushEvt = new WeakMap();
_onPopEvt = new WeakMap();
_onReplaceEvt = new WeakMap();
_ownNavs = new WeakMap();
_onForkEvt = new WeakMap();
_onActivicedEvt = new WeakMap();
_onDestroyEvt = new WeakMap();
export { BfcsNavigator };
//# sourceMappingURL=BfcsNavigator.mjs.map
