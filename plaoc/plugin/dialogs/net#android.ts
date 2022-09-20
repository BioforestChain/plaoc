import { netCallNative } from "../common/network";
import { Dialogs } from "./bfcsDialogsType";
import { NativeUI } from "../common/nativeHandle";

export class DialogsFFI implements Dialogs.IDialogsFFI {
  async openAlert(
    config: Dialogs.IAlertConfig,
    confirmFunc: string
  ): Promise<void> {
    let cb: string = `(()=>{
          ${confirmFunc}
      })`;
    await netCallNative(NativeUI.OpenDialogAlert, {
      config: JSON.stringify(config),
      cb,
    });
  }

  async openPrompt(
    config: Dialogs.IPromptConfig,
    confirmFunc: string,
    cancelFunc?: string
  ): Promise<void> {
    let cb: string = `((result)=>{
        if(result){
          ${confirmFunc}
        }else{
          ${cancelFunc ?? ""}
        }
      })`;
    await netCallNative(NativeUI.OpenDialogPrompt, {
      config: JSON.stringify(config),
      cb,
    });
  }

  async openConfirm(
    config: Dialogs.IConfirmConfig,
    confirmFunc: string,
    cancelFunc?: string
  ): Promise<void> {
    let cb: string = `((result)=>{
        if(result){
          ${confirmFunc}
        }else{
          ${cancelFunc ?? ""}
        }
      })`;
    await netCallNative(NativeUI.OpenDialogConfirm, {
      config: JSON.stringify(config),
      cb,
    });
  }

  async openWarning(
    config: Dialogs.IConfirmConfig,
    confirmFunc: string,
    cancelFunc?: string
  ): Promise<void> {
    let cb: string = `((result)=>{
        if(result){
          ${confirmFunc}
        }else{
          ${cancelFunc ?? ""}
        }
      })`;
    await netCallNative(NativeUI.OpenDialogWarning, {
      config: JSON.stringify(config),
      cb,
    });
  }
}
