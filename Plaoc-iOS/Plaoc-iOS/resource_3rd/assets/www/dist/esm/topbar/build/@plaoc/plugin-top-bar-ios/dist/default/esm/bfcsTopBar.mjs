import { TopBarFFI as e$1 } from "./ffi_ios.mjs";
class e extends HTMLElement {
  constructor() {
    super(), this._actionList = [], this._ffi = new e$1();
  }
  connectedCallback() {
    new MutationObserver((t) => {
      this.collectActions();
    }).observe(this, { subtree: true, childList: true, attributes: true, attributeFilter: ["disabled", "icon", "type", "description", "size", "source"] })
  }
  disconnectedCallback() {
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
    return await this._ffi.getBackgroundColor();
  }
  async setBackgroundColor(t = "#ffffff") {
    const e2 = { color: t, alpha: this.getAttribute("alpha") && this.getAttribute("alpha").length > 0 ? parseFloat(this.getAttribute("alpha")) : 0.5 };
    await this._ffi.setBackgroundColor(e2);
  }
  async getForegroundColor() {
    return await this._ffi.getForegroundColor();
  }
  async setForegroundColor(t = "#ffffff") {
    const e2 = { color: t, alpha: this.getAttribute("alpha") && this.getAttribute("alpha").length > 0 ? parseFloat(this.getAttribute("alpha")) : 0.5 };
    await this._ffi.setForegroundColor(e2);
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
      const i = `document.querySelector('dweb-top-bar-button[bid="${t.getAttribute("bid")}"]').dispatchEvent(new CustomEvent('click'))`, s = !!t.hasAttribute("disabled");
      this._actionList.push({ icon: e2, onClickCode: i, disabled: s });
    }), await this.setActions();
  }
  static get observedAttributes() {
    return ["title", "disabled", "backgroudColor", "foregroundColor", "overlay", "alpha"];
  }
  attributeChangedCallback(t, e2, i) {
    t === "title" ? this.setTitle(i) : t === "backgroudColor" ? this.setBackgroundColor(i) : t === "foregroundColor" ? this.setForegroundColor(i) : t === "overlay" ? this.hasAttribute(t) && this._ffi.setOverlay() : t === "disabled" && this.hasAttribute(t) && this._ffi.setHidden();
  }
}
export { e as BfcsTopBar };
//# sourceMappingURL=bfcsTopBar.mjs.map
