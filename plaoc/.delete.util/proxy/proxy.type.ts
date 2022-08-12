declare global {
  export interface XMLHttpRequestProxy extends XMLHttpRequest {
    responseText: string;
    readyState: number;
    response: any;
    responseURL: string;
    responseXML: Document | null;
    status: number;
    statusText: string;
    xhr: OriginXMLHttpRequest;
    [key: string]: any;
  }

  export interface OriginXMLHttpRequest extends XMLHttpRequest {
    [x: string]: any;
    config: any;
    resHeader: any;
    getProxy(): XMLHttpRequestProxy;
  }

  export interface AttrGetterAndSetter<T = any> {
    getter?: (value: T, xhr: OriginXMLHttpRequest) => T;
    setter?: (value: T, xhr: OriginXMLHttpRequest) => T;
  }

  export interface XhrRequestConfig {
    method: string;
    url: string;
    headers: any;
    body: any;
    async: boolean;
    user: string;
    password: string;
    withCredentials: boolean;
    xhr: OriginXMLHttpRequest;
  }

  export interface XhrResponse {
    config: XhrRequestConfig;
    headers: any;
    response: any;
    status: number;
    statusText?: string;
  }

  export type XhrErrorType =
    | "error"
    | "timeout"
    | "abort"
    | "load"
    | "loadend"
    | "readystatechange";

  export interface XhrError {
    config: XhrRequestConfig;
    type: XhrErrorType;
    error: any;
  }

  export interface Hooks {
    [key: string]: any;
    onreadystatechange?:
      | ((
          this: XMLHttpRequestProxy,
          xhr: OriginXMLHttpRequest,
          ev: ProgressEvent
        ) => any)
      | null;
    onabort?: ((xhr: OriginXMLHttpRequest, e: XhrError) => boolean) | null;
    onerror?: ((xhr: OriginXMLHttpRequest, e: XhrError) => boolean) | null;
    onload?:
      | ((
          this: XMLHttpRequestProxy,
          xhr: OriginXMLHttpRequest,
          ev: ProgressEvent
        ) => any)
      | null;
    onloadend?:
      | ((
          this: XMLHttpRequestProxy,
          xhr: OriginXMLHttpRequest,
          ev: ProgressEvent
        ) => any)
      | null;
    onloadstart?:
      | ((
          this: XMLHttpRequestProxy,
          xhr: OriginXMLHttpRequest,
          ev: ProgressEvent
        ) => any)
      | null;
    onprogress?:
      | ((
          this: XMLHttpRequestProxy,
          xhr: OriginXMLHttpRequest,
          ev: ProgressEvent
        ) => any)
      | null;
    ontimeout?: ((xhr: OriginXMLHttpRequest, e: XhrError) => boolean) | null;
    abort?: (args: Array<any>, xhr: OriginXMLHttpRequest) => any;
    getAllResponseHeaders?: (
      args: Array<any>,
      xhr: OriginXMLHttpRequest
    ) => any;
    getResponseHeader?: (args: Array<any>, xhr: OriginXMLHttpRequest) => any;
    open?: (args: Array<any>, xhr: OriginXMLHttpRequest) => any;
    overrideMimeType?: (args: Array<any>, xhr: OriginXMLHttpRequest) => any;
    send?: (args: Array<any>, xhr: OriginXMLHttpRequest) => any;
    setRequestHeader?: (args: Array<any>, xhr: OriginXMLHttpRequest) => any;
    addEventListener?: (args: Array<any>, xhr: OriginXMLHttpRequest) => any;
    removeEventListener?: (args: Array<any>, xhr: OriginXMLHttpRequest) => any;

    response?: AttrGetterAndSetter;
    responseText?: AttrGetterAndSetter<string>;
    readyState?: AttrGetterAndSetter<number>;
    responseType?: AttrGetterAndSetter<XMLHttpRequestResponseType>;
    responseURL?: AttrGetterAndSetter<string>;
    responseXML?: AttrGetterAndSetter<Document | null>;
    status?: AttrGetterAndSetter<number>;
    statusText?: AttrGetterAndSetter<string>;
    timeout?: AttrGetterAndSetter<number>;
    upload?: AttrGetterAndSetter<XMLHttpRequestUpload>;
    withCredentials?: AttrGetterAndSetter<boolean>;
  }

  export interface XhrHandler {
    resolve(response: XhrResponse): void;

    reject(err: XhrError): void;
  }

  export interface XhrRequestHandler extends XhrHandler {
    next(config: XhrRequestConfig): void;
  }

  export interface XhrResponseHandler extends XhrHandler {
    next(response: XhrResponse): void;
  }

  export interface XhrErrorHandler extends XhrHandler {
    next(error: XhrError): void;
  }

  export interface Proxy {
    onRequest?: (config: XhrRequestConfig, handler: XhrRequestHandler) => void;
    onResponse?: (response: XhrResponse, handler: XhrResponseHandler) => void;
    onError?: (err: XhrError, handler: XhrErrorHandler) => void;
    [key: string]: any;
  }

  export interface IProxy extends Proxy {}

  export interface DWindow extends Window {
    [key: string]: any;
  }
  export interface DEvent {
    [key: string]: any;
  }
}
export {};
