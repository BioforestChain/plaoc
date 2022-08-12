import { DwebPlugin } from "../native/dweb-plugin";
export class BfspIcon extends DwebPlugin {
  // private _icon: Plaoc.IPlaocIcon = { source: "", type: IconType.NamedIcon };

  constructor() {
    super();
  }

  connectedCallback() {}

  disconnectedCallback() {}

  static get observedAttributes() {
    return ["type", "description", "size", "source"];
  }

  attributeChangedCallback(attrName: string, oldVal: unknown, newVal: unknown) {
    // this._icon.source = this.getAttribute("source") ?? "";
    // this._icon.type = this.hasAttribute("type")
    //   ? (this.getAttribute("type") as Plaoc.IconType)
    //   : IconType.NamedIcon;
    // this._icon.description = this.getAttribute("description") ?? "";
    // this._icon.size = this.hasAttribute("size")
    //   ? (this.getAttribute("size") as unknown as number)
    //   : undefined;
    // this.parentElement?.setAttribute("icon", JSON.stringify(this._icon));
  }

  // adoptedCallback() {}
}
