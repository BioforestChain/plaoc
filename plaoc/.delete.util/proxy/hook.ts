const realXhr = "__xhr";

export let events: XhrErrorType[] = [
  "load",
  "loadend",
  "timeout",
  "error",
  "readystatechange",
  "abort",
];

export function configEvent(event: DEvent, xhrProxy: IProxy) {
  let e: DEvent = {} as DEvent;
  for (let attr in event) e[attr] = event[attr];
  e.target = e.currentTarget = xhrProxy;
  return e;
}

export function hook(proxy: Hooks, win: DWindow): XMLHttpRequest {
  win = win || window;
  win[realXhr] = win[realXhr] || win.XMLHttpRequest;
  win.XMLHttpRequest = function () {
    // 创建真正的代理
    const xhr: Hooks = new win![realXhr]();
    // 生成所有回调（例如 onload）都是可枚举的（不是未定义的）。
    for (let i = 0; i < events.length; ++i) {
      let key = "on" + events[i];
      if (xhr[key] === undefined) xhr[key] = null;
    }

    for (let attr in xhr) {
      let type = "";
      try {
        type = typeof xhr[attr]; // 防止发生异常
      } catch (e) {}
      if (type === "function") {
        // 代理所有的方法
        this[attr] = hookFunction(this, attr);
      } else {
        // 代理所有的属性
        Object.defineProperty(this, attr, {
          get: getterFactory(this, attr),
          set: setterFactory(this, attr),
          enumerable: true,
        });
      }
    }
    xhr.getProxy = () => {
      return this;
    };
    this.xhr = xhr;
  };

  Object.assign(win.XMLHttpRequest, {
    UNSENT: 0,
    OPENED: 1,
    HEADERS_RECEIVED: 2,
    LOADING: 3,
    DONE: 4,
  });
  function getterFactory(that: DWindow, attr: string) {
    return function () {
      var v = that.hasOwnProperty(attr + "_")
        ? that[attr + "_"]
        : that.xhr[attr];
      var attrGetterHook = (proxy[attr] || {})["getter"];
      return (attrGetterHook && attrGetterHook(v, that)) || v;
    };
  }
  function setterFactory(that: DWindow, attr: string) {
    return function (v: Function) {
      const xhr = that.xhr;
      const hook = proxy[attr];
      // hookAjax 事件回调，如`onload`、`onreadystatechange`...
      if (attr.substring(0, 2) === "on") {
        that[attr + "_"] = v;
        xhr[attr] = (e: DEvent) => {
          e = configEvent(e, that);
          let ret = proxy[attr] && proxy[attr].call(that, xhr, e);
          ret || v.call(that, e);
        };
      } else {
        //如果属性不可写，则生成代理属性
        let attrSetterHook = (hook || {})["setter"];
        v = (attrSetterHook && attrSetterHook(v, that)) || v;
        that[attr + "_"] = v;
        try {
          // 并非 xhr 的所有属性都是可写的（setter 可能未定义）。
          xhr[attr] = v;
        } catch (e) {}
      }
    };
  }
  // 代理方法
  function hookFunction(that: DWindow, attr: string) {
    return function () {
      let args = [].slice.call(arguments);
      if (proxy[attr]) {
        let ret = proxy[attr].call(that, args, that.xhr);
        // 如果代理返回值存在，直接返回，
        // 否则调用 xhr 的函数。
        if (ret) return ret;
      }
      console.log(that.xhr, args);
      return that.xhr[attr].apply(that.xhr, args);
    };
  }
  // 返回真正的 XMLHttpRequest
  return win[realXhr];
}

export function unHook(win?: DWindow) {
  win = win || window;
  if (win[realXhr]) win.XMLHttpRequest = win[realXhr];
  win[realXhr] = undefined;
}
