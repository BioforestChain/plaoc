export class DialogsFFI implements Plaoc.IDialogsFFI {
  private _ffi: Plaoc.DialogsAndroidFFI = native_dialog;

  openAlert(config: Plaoc.IAlertConfig, confirmFunc: string): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      this._ffi.openAlert(JSON.stringify(config), confirmFunc);

      resolve();
    });
  }

  openPrompt(
    config: Plaoc.IPromptConfig,
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
    config: Plaoc.IConfirmConfig,
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
    config: Plaoc.IConfirmConfig,
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
