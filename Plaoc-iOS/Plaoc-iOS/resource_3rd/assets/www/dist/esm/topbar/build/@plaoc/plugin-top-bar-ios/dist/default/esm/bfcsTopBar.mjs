import { TopBarFFI as e$1 } from "./ffi_ios.mjs";
class e extends HTMLElement {
  constructor() {
    super(), this._actionList = [], this._alpha = "ff", this._ffi = new e$1(), this._observer = new MutationObserver((t) => {
      this.collectActions();
    }), this._init();
  }
  connectedCallback() {
    this._observer.observe(this, { subtree: true, childList: true, attributes: true });
  }
  disconnectedCallback() {
    this._observer.disconnect();
  }
  async _init() {
    this._setAlpha();
    const t = await this.getHeight();
    this.setAttribute("height", `${t}`);
  }
  _setAlpha() {
    if (this.hasAttribute("alpha") && this.getAttribute("alpha") && this.getAttribute("alpha").length > 0)
      try {
        const t = parseFloat(this.getAttribute("alpha")), e2 = Math.round(255 * t).toString(16);
        e2.length > 1 ? this._alpha = e2 : this._alpha = "0" + e2;
      } catch {
      }
  }
  async back() {
    await this._ffi.back();
  }
  async toggleEnabled() {
    await this._ffi.toggleEnabled();
  }
  async getEnabled() {
    return await this._ffi.getEnabled();
  }
  async getTitle() {
    return await this._ffi.getTitle();
  }
  async setTitle(t) {
    await this._ffi.setTitle(t);
  }
  async hasTitle() {
    return await this._ffi.hasTitle();
  }
  async getOverlay() {
    return await this._ffi.getOverlay();
  }
  async toggleOverlay() {
    await this._ffi.toggleOverlay();
  }
  async getHeight() {
    return await this._ffi.getHeight();
  }
  async getBackgroundColor() {
    return await this._ffi.getBackgroundColor(this._alpha);
  }
  async setBackgroundColor(t) {
    await this._ffi.setBackgroundColor(t, this._alpha);
  }
  async getForegroundColor() {
    return await this._ffi.getForegroundColor(this._alpha);
  }
  async setForegroundColor(t) {
    await this._ffi.setForegroundColor(t, this._alpha);
  }
  async getActions() {
    return this._actionList = await this._ffi.getActions(), this._actionList;
  }
  async setActions() {
    await this._ffi.setActions(this._actionList);
  }
  async collectActions() {
    this._actionList = [], this.querySelectorAll("dweb-top-bar-button").forEach((t) => {
      let e2 = { source: "", type: "NamedIcon" };
      if (t.querySelector("dweb-icon")) {
        let i2 = t.querySelector("dweb-icon");
        e2.source = i2?.getAttribute("source") ?? "", e2.type = i2?.hasAttribute("type") ? i2.getAttribute("type") : "NamedIcon", e2.description = i2?.getAttribute("description") ?? "", e2.size = i2?.hasAttribute("size") ? i2.getAttribute("size") : void 0;
      }
      const i = `document.querySelector('dweb-top-bar-button[bid="${t.getAttribute("bid")}"]').dispatchEvent(new CustomEvent('click'))`, a = !!t.hasAttribute("disabled");
      this._actionList.push({ icon: e2, onClickCode: i, disabled: a });
    }), await this.setActions();
  }
  static get observedAttributes() {
    return ["title", "hidden", "background-color", "foreground-color", "overlay"];
  }
  async attributeChangedCallback(t, e2, i) {
    t === "title" ? await this.setTitle(i) : t === "background-color" ? await this.setBackgroundColor(i) : t === "foreground-color" ? await this.setForegroundColor(i) : t === "overlay" ? this.hasAttribute(t) && await this._ffi.setOverlay() : t === "hidden" && this.hasAttribute(t) && await this._ffi.setHidden();
  }
}
export { e as BfcsTopBar };
//# sourceMappingURL=bfcsTopBar.mjs.map
