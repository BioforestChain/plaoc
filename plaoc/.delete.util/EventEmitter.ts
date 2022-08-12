export class EventEmitter {
  private events: any;
  constructor() {
    this.events = {};
  }

  on(eventName: string, callback: Function) {
    if (this.events[eventName]) {
      if (eventName !== "newListener") {
        this.emit("newListener", eventName);
      }
    }
    const callbacks = this.events[eventName] || [];
    callbacks.push(callback);
    this.events[eventName] = callbacks;
  }

  once(eventName: string, callback: Function) {
    const one = (...args: any[]) => {
      callback(...args);
      this.off(eventName, one);
    };
    one.initialCallback = callback;
    this.on(eventName, one);
  }

  emit(eventName: string, ...args: any[]) {
    const callbacks = this.events[eventName] || [];
    callbacks.forEach((cb: Function) => cb(...args));
  }

  off(eventName: string, callback: Function) {
    const callbacks = this.events[eventName] || [];

    const newCallbacks = callbacks.filter(
      (fn: any) =>
        fn != callback &&
        fn.initialCallback != callback /* 用于once的取消订阅 */
    );

    this.events[eventName] = newCallbacks;
  }
}
