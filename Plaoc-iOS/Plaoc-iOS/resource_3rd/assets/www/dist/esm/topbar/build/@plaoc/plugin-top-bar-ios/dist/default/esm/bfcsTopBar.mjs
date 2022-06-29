import { TopBarFFI as e$1 } from "./ffi_ios.mjs";
class e extends HTMLElement {
  constructor() {
    super(), this._actionList = [], this._ffi = new e$1(), this._observer = new MutationObserver((t) => {
      this.collectActions();
    });
  }
  connectedCallback() {
    this._observer.observe(this, { subtree: true, childList: true, attributes: true });
  }
  disconnectedCallback() {
    this._observer.disconnect();
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
      alert(JSON.stringify(e2));
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
      const i = `document.querySelector('dweb-top-bar-button[bid="${t.getAttribute("bid")}"]').dispatchEvent(new CustomEvent('click'))`, a = !!t.hasAttribute("disabled");
      this._actionList.push({ icon: e2, onClickCode: i, disabled: a });
    }), await this.setActions();
  }
  static get observedAttributes() {
    return ["title", "disabled", "background-color", "foreground-color", "overlay", "alpha"];
  }
  async attributeChangedCallback(t, e2, i) {
    t === "title" ? await this.setTitle(i) : t === "background-color" ? await this.setBackgroundColor(i) : t === "foreground-color" ? await this.setForegroundColor(i) : t === "overlay" ? this.hasAttribute(t) && await this._ffi.setOverlay() : t === "disabled" && this.hasAttribute(t) && await this._ffi.setHidden();
  }
}
export { e as BfcsTopBar };
//# sourceMappingURL=bfcsTopBar.mjs.map
