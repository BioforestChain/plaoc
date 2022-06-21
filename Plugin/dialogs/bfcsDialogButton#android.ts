export class BfcsDialogButton extends HTMLElement {
  constructor() {
    super();
  }

  connectedCallback() {
    this.setAttribute("bid", (Math.random() * Date.now()).toFixed(0));

    this.setAttribute("label", this.innerHTML);
    this.innerHTML = "";
  }
}
