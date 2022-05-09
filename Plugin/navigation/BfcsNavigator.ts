import { EvtOut } from "./EvtOut";

export class BfcsNavigator<R extends Route = Route> {
  constructor(
    /**当前导航器的信息 */
    readonly info: BfcsNavigator.NavigatorInfo,
    /**父级导航器的公开信息 */
    readonly parentInfo: BfcsNavigator.NavigatorInfo | undefined,
    /**
     * Foreign Function Interface
     */
    private _ffi: BfcsNavigator.FFI
  ) {}

  #routes: R[] = [];
  /**
   * 当前存有的 route 个数
   */
  get length() {
    return this.#routes.length;
  }
  /**
   * 根据下标读取路由
   */
  at(index: number): R | undefined {
    return this.#routes[index < 0 ? this.#routes.length + index : index];
  }

  /**
   * 不能连续 push 重复的 route
   * @param route
   */
  push(route: R): boolean {
    const success = this._ffi.push(this.info.nid, route);
    if (success) {
      this.#routes.push(route);
      this.#onPushEvt.emit({ route });
    }
    return success;
  }
  #onPushEvt = new EvtOut<{ route: R }>();
  get onPush() {
    return this.#onPushEvt.toAsyncGenerator();
  }
  /**
   * 返回真正 pop 出来的数量
   * @param count
   */
  pop(count: number = 1): number {
    const result = this._ffi.pop(this.info.nid, count);
    if (result > 0) {
      for (const route of this.#routes.splice(-result)) {
        this.#onPopEvt.emit({ route });
      }
    }
    return result;
  }
  #onPopEvt = new EvtOut<{ route: R }>();
  get onPop() {
    return this.#onPopEvt.toAsyncGenerator();
  }
  /**
   * 替代当前的路由栈中最后一个
   * 不能连续 replace 重复的 route
   * @param route
   */
  replace(route: R, at = -1): boolean {
    const index = at < 0 ? this.#routes.length + at : at;
    if (index in this.#routes === false) {
      return false;
    }
    const success = this._ffi.replace(this.info.nid, index, route);
    if (success) {
      const removedRoutes = this.#routes.splice(at, 1, route);
      this.#onReplaceEvt.emit({ newRoute: route, oldRoute: removedRoutes[0] });
    }
    return success;
  }
  #onReplaceEvt = new EvtOut<{ newRoute: R; oldRoute: R }>();
  get onReplace() {
    return this.#onReplaceEvt.toAsyncGenerator();
  }
  //   /**
  //    * 阻止默认返回行为，会提供“连续进行返回操作的次数”信息，使用场景如下：
  //    * 1. 重要弹窗需要用户明确点击按钮做出反应，那么可以默认阻止弹窗关闭，如果用户连续进行两次返回操作，就触发默认按钮的点击结果
  //    * 2. actionSheet（在桌面端下就是右键菜单）的关闭
  //    * 3. 简单窗口的关闭或隐藏
  //    * 4. 根应用（桌面应用）不能再返回
  //    */
  //   protected abstract _guard(reason: PromiseLike<unknown>, times?: number);
  //   readonly onGuard: Evt<{ times: number; reason: PromiseLike<unknown> }>;

  #ownNavs = new WeakSet<BfcsNavigator>();
  /**
   * 创新一个新的导航器
   * @param opts
   */
  fork(opts: BfcsNavigator.ForkOptions): BfcsNavigator | undefined {
    // const parentNav = this
    //   opts.fromNavigator === null ? null : opts.fromNavigator || this;
    // const parentNid = parentNav?.info.nid ?? -1;
    const result = this._ffi.fork(this.info.nid, opts.data);
    if (result >= 0) {
      const newNavigator = new BfcsNavigator(
        Object.freeze({ data: opts.data, nid: result }),
        this.info,
        this._ffi
      );
      this.#ownNavs.add(newNavigator);
      this.#onForkEvt.emit({
        newNavigator: newNavigator,
        fromNavigator: this,
      });
      return newNavigator;
    }
  }
  #onForkEvt = new EvtOut<{
    newNavigator: BfcsNavigator;
    fromNavigator: BfcsNavigator | null;
  }>();
  get onFork() {
    return this.#onForkEvt.toAsyncGenerator();
  }

  /**
   * 切换导航器，切换出来后，当前 navigator 就不能再操作
   * 只能切换自己 fork 出来的子路由 或者 自身
   *
   * 如果无权切换，那么会返回 false
   */
  checkout(navigator: BfcsNavigator): boolean {
    if (this.#ownNavs.has(navigator) === false) {
      return false;
    }
    return this._ffi.checkout(this.info.nid, navigator.info.nid);
    // if (success) {
    //   this.#onCheckoutEvt.emit({
    //     fromNavigator: this, // 应该使用当前正在激活的 nav
    //     toNavigator: navigator,
    //   });
    // }
    // return success;
  }
  #onActivicedEvt = new EvtOut<{
    fromNavigator: BfcsNavigator;
    toNavigator: BfcsNavigator;
  }>();
  get onActiviced() {
    return this.#onActivicedEvt.toAsyncGenerator();
  }

  /**
   * 销毁导航器
   * 只能销毁自己 fork 出来的子路由 或者 自身
   *
   * 如果无权切换，那么会返回 false
   * @param navigator
   */
  destroy(navigator: BfcsNavigator, reason?: unknown): boolean {
    if (this.#ownNavs.has(navigator) === false) {
      return false;
    }
    const success = this._ffi.destroy(this.info.nid, navigator.info.nid);
    if (success) {
      this.#ownNavs.delete(navigator);
      navigator.#onDestroyEvt.emit({ reason });
    }
    return success;
  }
  #onDestroyEvt = new EvtOut<{
    reason?: unknown;
  }>();

  get onDestroy() {
    return this.#onDestroyEvt.toAsyncGenerator();
  }
}
type Cloneable = string | number | { [key: string]: Cloneable };

type Route = Cloneable;

export namespace BfcsNavigator {
  export type FFI = {
    init(): string;
    push(nid: number, route: Route): boolean;
    pop(nid: number, count: number): number;
    replace(nid: number, at: number, newRoute: Route): boolean;
    fork(nid: number, data: Cloneable): number;
    checkout(nid: number, toNid: number): boolean;
    destroy(nid: number, targetNid: number): boolean;
    onActivited: EvtOut<{ fromNid: number; toNid: number }>;
    onUnActivited: EvtOut<{ fromNid: number; toNid: number }>;
  };
  export type NavigatorInfo = Readonly<{
    nid: number;
    //   maxGuardTimes: number;
    data: Cloneable;
  }>;
  export type ForkOptions = {
    data: Cloneable;
    // /**
    //  * 意味着新导航器能拦截的连续点击返回的最大次数，只能设置不多于当前导航器的拦截数。
    //  * 默认与当前导航器的最大拦截数一致。
    //  */
    // maxGuardTimes?: number;

    // /**
    //  * 能为其声明来源。默认会提供当前 nav 对象。
    //  * 如果设置成 null ，那么就是脱离父级存在。
    //  */
    // fromNavigator?: BfcsNavigator | null;
  };
}
