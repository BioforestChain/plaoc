export class Observer {
  constructor() {
    const stream = new ReadableStream({
      start: (controller) => {
        this.next = (v) => controller.enqueue(v);
      },
    });
    this.toAsyncIterator = () => {
      // Get a lock on the stream:
      const reader = stream.getReader();

      return {
        next() {
          // Stream reads already resolve with {done, value}, so
          // we can just call read:
          return reader.read();
        },
        return() {
          // Release the lock if the iterator terminates.
          reader.releaseLock();
          return {};
        },
        // for-await calls this on whatever it's passed, so
        // iterators tend to return themselves.
        [Symbol.asyncIterator]() {
          return this;
        },
      };
    };
  }

  get value() {
    this._value;
  }
  set value(v) {
    if (this._value === v) {
      return;
    }
    this._value = v;
    this.next(v);
  }
}
