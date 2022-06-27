class e {
  constructor() {
    this._ffi = window.webkit.messageHandlers;
  }
  back() {
    return new Promise((e2, s) => {
      this._ffi.back.postMessage(null), e2();
    });
  }
  async getEnabled() {
    return !await this._ffi.getNaviEnabled.postMessage(null);
  }
  toggleEnabled() {
    return new Promise(async (e2, s) => {
      const t = await this.getEnabled();
      this._ffi.hiddenNaviBar.postMessage(t ? "1" : "0"), e2();
    });
  }
  async setHidden() {
    await this._ffi.hiddenNaviBar.postMessage("1");
  }
  async getOverlay() {
    return this._ffi.getNaviOverlay.postMessage(null);
  }
  toggleOverlay() {
    return new Promise(async (e2, s) => {
      const t = await this.getOverlay();
      this._ffi.updateNaviBarAlpha.postMessage(t ? "0" : "1"), e2();
    });
  }
  setOverlay() {
    return new Promise((e2, s) => {
      this._ffi.updateNaviBarAlpha.postMessage("1"), e2();
    });
  }
  async getTitle() {
    return this._ffi.getNaviTitle.postMessage(null);
  }
  setTitle(e2) {
    return new Promise((s, t) => {
      this._ffi.updateTitle.postMessage(e2), s();
    });
  }
  async hasTitle() {
    return await this._ffi.hasNaviTitle.postMessage(null);
  }
  async getHeight() {
    return await this._ffi.naviHeight.postMessage(null);
  }
  async getActions() {
    return await this._ffi.getNaviActions.postMessage(null);
  }
  setActions(e2) {
    return new Promise((s, t) => {
      this._ffi.customNaviActions.postMessage(e2), s();
    });
  }
  async getBackgroundColor() {
    return (await this._ffi.getNaviBackgroundColor.postMessage(null)).color;
  }
  async setBackgroundColor(e2) {
    this._ffi.updateNaviBarBackgroundColor.postMessage(e2);
  }
  async getForegroundColor() {
    return (await this._ffi.getNaviForegroundColor.postMessage(null)).color;
  }
  async setForegroundColor(e2) {
    this._ffi.updateNaviBarTintColor.postMessage(e2);
  }
}
export { e as TopBarFFI };
//# sourceMappingURL=ffi_ios.mjs.map
