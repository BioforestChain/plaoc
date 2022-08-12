import { hook, unHook, configEvent, events } from "./hook";

let eventLoad = events[0],
  eventLoadEnd = events[1],
  eventTimeout = events[2],
  eventError = events[3],
  eventReadyStateChange = events[4],
  eventAbort = events[5];

export function proxy(proxy: IProxy, win: DWindow): XMLHttpRequest {
  win = win || window;
  if (win["__xhr"]) throw "Ajax is already hooked.";

  return proxyAjax(proxy, win);
}

export function unProxy(win?: Window) {
  unHook(win);
}

export function trim(str: string) {
  return str.replace(/^\s+|\s+$/g, "");
}

export function getEventTarget(xhr: any) {
  return xhr.watcher || (xhr.watcher = document.createElement("a"));
}

export function triggerListener(xhr: OriginXMLHttpRequest, name: string) {
  let xhrProxy = xhr.getProxy();
  let callback = "on" + name + "_";
  let event = configEvent({ type: name }, xhrProxy);
  xhrProxy[callback] && xhrProxy[callback](event);
  let evt;
  if (typeof Event === "function") {
    evt = new Event(name, { bubbles: false });
  } else {
    // ie11
    evt = document.createEvent("Event");
    evt.initEvent(name, false, true);
  }
  getEventTarget(xhr).dispatchEvent(evt);
}

class Header {
  xhr: OriginXMLHttpRequest;
  xhrProxy: IProxy;
  constructor(xhr: OriginXMLHttpRequest) {
    this.xhr = xhr;
    this.xhrProxy = xhr.getProxy();
  }
  resolve(response: XhrResponse) {
    let xhrProxy = this.xhrProxy;
    let xhr = this.xhr;

    xhrProxy.readyState = 4;
    xhr.resHeader = response.headers;
    xhrProxy.response = xhrProxy.responseText = response.response;
    xhrProxy.statusText = response.statusText;
    xhrProxy.status = response.status;
    triggerListener(xhr, eventReadyStateChange);
    triggerListener(xhr, eventLoad);
    triggerListener(xhr, eventLoadEnd);
  }
  reject(error: XhrError) {
    this.xhrProxy.status = 0;
    triggerListener(this.xhr, error.type);
    triggerListener(this.xhr, eventLoadEnd);
  }
}

class RequestHandler extends Header {
  next(rq: XhrRequestConfig) {
    const xhr = this.xhr;
    rq = rq || xhr.config;
    xhr.withCredentials = rq.withCredentials;
    xhr.open(rq.method, rq.url, rq.async !== false, rq.user, rq.password);
    for (const key in rq.headers) {
      xhr.setRequestHeader(key, rq.headers[key]);
    }
    xhr.send(rq.body);
  }
}

class ResponseHandler extends Header {
  next(response: XhrResponse) {
    this.resolve(response);
  }
}

class ErrorHandler extends Header {
  next(error: XhrError) {
    this.reject(error);
  }
}

export function proxyAjax(proxy: IProxy, win: DWindow) {
  const onRequest = proxy.onRequest,
    onResponse = proxy.onResponse,
    onError = proxy.onError;
  function handleResponse(xhr: OriginXMLHttpRequest, xhrProxy: IProxy) {
    let handler = new ResponseHandler(xhr);
    let ret = {
      response: xhrProxy.response || xhrProxy.responseText, //ie9
      status: xhrProxy.status,
      statusText: xhrProxy.statusText,
      config: xhr.config,
      headers:
        xhr.resHeader ||
        xhr
          .getAllResponseHeaders()
          .split("\r\n")
          .reduce(function (ob: { [x: string]: string }, str: string) {
            if (str === "") return ob;
            let m: any = str.split(":");
            ob[m.shift()] = trim(m.join(":"));
            return ob;
          }, {}),
    };
    if (!onResponse) return handler.resolve(ret);
    onResponse(ret, handler);
  }

  function onerror(
    xhr: OriginXMLHttpRequest,
    error: XhrError,
    errorType: XhrErrorType
  ) {
    let handler = new ErrorHandler(xhr);
    error = { config: xhr.config, error: error, type: errorType };
    if (onError) {
      onError(error, handler);
    } else {
      handler.next(error);
    }
  }

  function preventXhrProxyCallback() {
    return true;
  }

  function errorCallback(errorType: XhrErrorType) {
    return function (xhr: OriginXMLHttpRequest, e: XhrError) {
      onerror(xhr, e, errorType);
      return true;
    };
  }

  function stateChangeCallback(xhr: OriginXMLHttpRequest, xhrProxy: IProxy) {
    if (xhr.readyState === 4 && xhr.status !== 0) {
      handleResponse(xhr, xhrProxy);
    } else if (xhr.readyState !== 4) {
      triggerListener(xhr, eventReadyStateChange);
    }
    return true;
  }
  return hook(
    {
      onload: preventXhrProxyCallback,
      onloadend: preventXhrProxyCallback,
      onerror: errorCallback(eventError),
      ontimeout: errorCallback(eventTimeout),
      onabort: errorCallback(eventAbort),
      onreadystatechange: function (xhr: OriginXMLHttpRequest) {
        return stateChangeCallback(xhr, this);
      },
      open: function open(args: any[], xhr: OriginXMLHttpRequest) {
        const _this = this;
        const config: XhrRequestConfig = (xhr.config = {
          headers: {},
        }) as XhrRequestConfig;
        config.method = args[0];
        config.url = args[1];
        config.async = args[2];
        config.user = args[3];
        config.password = args[4];
        config.xhr = xhr;
        const evName = "on" + eventReadyStateChange;
        if (!xhr[evName]) {
          xhr[evName] = function () {
            return stateChangeCallback(xhr, _this);
          };
        }
        // 如果有请求拦截器，则在调用onRequest后再打开链接。因为onRequest最佳调用时机是在send前，
        // 所以我们在send拦截函数中再手动调用open，因此返回true阻止xhr.open调用。
        // 如果没有请求拦截器，则不用阻断xhr.open调用
        if (onRequest) return true;
      },
      send: function (args: any[], xhr: OriginXMLHttpRequest) {
        const config = xhr.config;
        config.withCredentials = xhr.withCredentials;
        config.body = args[0];
        if (onRequest) {
          const req = function () {
            onRequest!(config, new RequestHandler(xhr));
          };
          config.async === false ? req() : setTimeout(req);
          return true;
        }
      },
      setRequestHeader: function (args: any[], xhr: OriginXMLHttpRequest) {
        xhr.config.headers[args[0].toLowerCase()] = args[1];
        if (onRequest) return true;
      },
      addEventListener: function (args: any, xhr: OriginXMLHttpRequest) {
        const _this = this;
        if (events.indexOf(args[0]) !== -1) {
          let handler = args[1];
          getEventTarget(xhr).addEventListener(args[0], function (e: DEvent) {
            let event: DEvent = configEvent(e, _this);
            event.type = args[0];
            event.isTrusted = true;
            handler.call(_this, event);
          });
          return true;
        }
      },
      getAllResponseHeaders: function (_: any, xhr: OriginXMLHttpRequest) {
        const headers = xhr.resHeader;
        if (headers) {
          let header = "";
          for (let key in headers) {
            header += key + ": " + headers[key] + "\r\n";
          }
          return header;
        }
      },
      getResponseHeader: function (args: any[], xhr: OriginXMLHttpRequest) {
        let headers = xhr.resHeader;
        if (headers) {
          return headers[(args[0] || "").toLowerCase()];
        }
      },
    },
    win
  );
}
