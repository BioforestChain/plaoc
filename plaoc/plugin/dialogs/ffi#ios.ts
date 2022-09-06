import { Dialogs } from "./bfcsDialogs.type";

export class DialogsFFI implements Dialogs.IDialogsFFI {
  private _ffi = (window as any).webkit
    .messageHandlers as Dialogs.DialogsIosFFI;

  openAlert(config: Dialogs.IAlertConfig, confirmFunc: string): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      const alertConfig: Dialogs.IAlertConfigIOS = {
        ...config,
        confirmFunc,
      };

      this._ffi.openAlert.postMessage(JSON.stringify(alertConfig));

      resolve();
    });
  }

  openPrompt(
    config: Dialogs.IPromptConfig,
    confirmFunc: string,
    cancelFunc?: string
  ): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      const promptConfig: Dialogs.IPromptConfigIOS = {
        ...config,
        confirmFunc,
        cancelFunc: cancelFunc ?? "",
      };

      this._ffi.openPrompt.postMessage(JSON.stringify(promptConfig));

      resolve();
    });
  }

  openConfirm(
    config: Dialogs.IConfirmConfig,
    confirmFunc: string,
    cancelFunc: string
  ): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      const confirmConfig: Dialogs.IConfirmConfigIOS = {
        ...config,
        confirmFunc,
        cancelFunc: cancelFunc ?? "",
      };

      this._ffi.openConfirm.postMessage(JSON.stringify(confirmConfig));

      resolve();
    });
  }

  openWarning(
    config: Dialogs.IConfirmConfig,
    confirmFunc: string,
    cancelFunc: string
  ): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      const beforeConfig: Dialogs.IConfirmConfigIOS = {
        ...config,
        confirmFunc,
        cancelFunc: cancelFunc ?? "",
      };

      this._ffi.openWarning.postMessage(JSON.stringify(beforeConfig));

      resolve();
    });
  }
}
