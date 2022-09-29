/// <reference lib="dom" />

import {
  BfcsDialogAlert,
  BfcsDialogConfirm,
  BfcsDialogPrompt,
  BfcsDialogWarning,
} from "./bfcsDialogs.ts";
import { BfcsDialogButton } from "./bfcsDialogButton.ts";

customElements.define("dweb-dialog-alert", BfcsDialogAlert);
customElements.define("dweb-dialog-prompt", BfcsDialogPrompt);
customElements.define("dweb-dialog-confirm", BfcsDialogConfirm);
customElements.define("dweb-dialog-warning", BfcsDialogWarning);
customElements.define("dweb-dialog-button", BfcsDialogButton);
