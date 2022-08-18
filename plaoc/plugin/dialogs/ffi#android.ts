import { Dialogs } from "./bfcsDialogs.type";

export class DialogsFFI implements Dialogs.IDialogsFFI {
  private _ffi: Dialogs.DialogsAndroidFFI = (window as any).native_dialog;

  openAlert(config: Dialogs.IAlertConfig, confirmFunc: string): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      this._ffi.openAlert(JSON.stringify(config), confirmFunc);

      resolve();
    });
  }

  openPrompt(
    config: Dialogs.IPromptConfig,
    confirmFunc: string,
    cancelFunc?: string
  ): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      let cb: string = `((result)=>{
        if(result){
          ${confirmFunc}
        }else{
          ${cancelFunc ?? ""}
        }
      })`;

      this._ffi.openPrompt(JSON.stringify(config), cb);

      resolve();
    });
  }

  openConfirm(
    config: Dialogs.IConfirmConfig,
    confirmFunc: string,
    cancelFunc?: string
  ): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      let cb: string = `((result)=>{
        if(result){
          ${confirmFunc}
        }else{
          ${cancelFunc ?? ""}
        }
      })`;

      this._ffi.openConfirm(JSON.stringify(config), cb);

      resolve();
    });
  }

  openBeforeUnload(
    config: Dialogs.IConfirmConfig,
    confirmFunc: string,
    cancelFunc?: string
  ): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      let cb: string = `((result)=>{
        if(result){
          ${confirmFunc}
        }else{
          ${cancelFunc ?? ""}
        }
      })`;

      this._ffi.openBeforeUnload(JSON.stringify(config), cb);

      resolve();
    });
  }
}
