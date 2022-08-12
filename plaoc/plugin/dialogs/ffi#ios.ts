export class DialogsFFI implements Plaoc.IDialogsFFI {
  private _ffi = (window as any).webkit.messageHandlers as Plaoc.DialogsIosFFI;

  openAlert(config: Plaoc.IAlertConfig, confirmFunc: string): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      const alertConfig: Plaoc.IAlertConfigIOS = {
        ...config,
        confirmFunc,
      };

      this._ffi.openAlert.postMessage(JSON.stringify(alertConfig));

      resolve();
    });
  }

  openPrompt(
    config: Plaoc.IPromptConfig,
    confirmFunc: string,
    cancelFunc?: string
  ): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      const promptConfig: Plaoc.IPromptConfigIOS = {
        ...config,
        confirmFunc,
        cancelFunc: cancelFunc ?? "",
      };

      this._ffi.openPrompt.postMessage(JSON.stringify(promptConfig));

      resolve();
    });
  }

  openConfirm(
    config: Plaoc.IConfirmConfig,
    confirmFunc: string,
    cancelFunc: string
  ): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      const confirmConfig: Plaoc.IConfirmConfigIOS = {
        ...config,
        confirmFunc,
        cancelFunc: cancelFunc ?? "",
      };

      this._ffi.openConfirm.postMessage(JSON.stringify(confirmConfig));

      resolve();
    });
  }

  openBeforeUnload(
    config: Plaoc.IConfirmConfig,
    confirmFunc: string,
    cancelFunc: string
  ): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      const beforeConfig: Plaoc.IConfirmConfigIOS = {
        ...config,
        confirmFunc,
        cancelFunc: cancelFunc ?? "",
      };

      this._ffi.openBeforeUnload.postMessage(JSON.stringify(beforeConfig));

      resolve();
    });
  }
}
