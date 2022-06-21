/// <reference lib="dom" />

import {
  BfcsDialogAlert,
  BfcsDialogPrompt,
  BfcsDialogConfirm,
  BfcsDialogBeforeUnload,
} from "#bfcsDialogs";
import { BfcsDialogButton } from "#bfcsDialogButton";

customElements.define("dweb-dialog-alert", BfcsDialogAlert);
customElements.define("dweb-dialog-prompt", BfcsDialogPrompt);
customElements.define("dweb-dialog-confirm", BfcsDialogConfirm);
customElements.define("dweb-dialog-before-unload", BfcsDialogBeforeUnload);
customElements.define("dweb-dialog-button", BfcsDialogButton);
