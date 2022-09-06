/// <reference lib="dom" />

import {
  BfcsDialogAlert,
  BfcsDialogPrompt,
  BfcsDialogConfirm,
  BfcsDialogWarning,
} from "./bfcsDialogs";
import { BfcsDialogButton } from "./bfcsDialogButton";

customElements.define("dweb-dialog-alert", BfcsDialogAlert);
customElements.define("dweb-dialog-prompt", BfcsDialogPrompt);
customElements.define("dweb-dialog-confirm", BfcsDialogConfirm);
customElements.define("dweb-dialog-warning", BfcsDialogWarning);
customElements.define("dweb-dialog-button", BfcsDialogButton);
