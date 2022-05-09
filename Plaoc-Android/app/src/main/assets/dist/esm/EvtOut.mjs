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
var __privateSet = (obj, member, value, setter) => {
  __accessCheck(obj, member, "write to private field");
  setter ? setter.call(obj, value) : member.set(obj, value);
  return value;
};
var __privateMethod = (obj, member, method) => {
  __accessCheck(obj, member, "access private method");
  return method;
};
var _controller, _stream, _toAsyncGenerator, toAsyncGenerator_fn;
class EvtOut {
  constructor() {
    __privateAdd(this, _toAsyncGenerator);
    __privateAdd(this, _controller, void 0);
    __privateAdd(this, _stream, new ReadableStream({
      start: (controller) => __privateSet(this, _controller, controller)
    }));
  }
  emit(data) {
    __privateGet(this, _controller).enqueue(data);
  }
  toAsyncGenerator() {
    const stream = __privateGet(this, _stream).tee()[1];
    const reader = stream.getReader();
    const ag = __privateMethod(this, _toAsyncGenerator, toAsyncGenerator_fn).call(this, reader);
    const _return = ag.return;
    ag.return = (value) => {
      reader.cancel();
      return _return.call(ag, value);
    };
    const _throw = ag.throw;
    ag.throw = (e) => {
      reader.cancel();
      return _throw.call(ag, e);
    };
    return ag;
  }
}
_controller = new WeakMap();
_stream = new WeakMap();
_toAsyncGenerator = new WeakSet();
toAsyncGenerator_fn = async function* (reader) {
  do {
    const result = await reader.read();
    if (result.done) {
      break;
    }
    yield result.value;
  } while (true);
};
export { EvtOut };
//# sourceMappingURL=EvtOut.mjs.map
