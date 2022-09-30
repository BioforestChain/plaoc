import { netCallNativeUi } from "@bfsx/gateway";
import { Dialogs } from "./bfcsDialogsType.ts";
import { NativeUI } from "../common/nativeHandle.ts";

export class DialogsFFI implements Dialogs.IDialogsFFI {
  async openAlert(
    config: Dialogs.IAlertConfig,
    confirmFunc: string,
  ): Promise<void> {
    const cb = `(()=>{
          ${confirmFunc}
      })`;
    await netCallNativeUi(NativeUI.OpenDialogAlert, {
      config: JSON.stringify(config),
      cb,
    });
  }

  async openPrompt(
    config: Dialogs.IPromptConfig,
    confirmFunc: string,
    cancelFunc?: string,
  ): Promise<void> {
    const cb = `((result)=>{
        if(result){
          ${confirmFunc}
        }else{
          ${cancelFunc ?? ""}
        }
      })`;
    await netCallNativeUi(NativeUI.OpenDialogPrompt, {
      config: JSON.stringify(config),
      cb,
    });
  }

  async openConfirm(
    config: Dialogs.IConfirmConfig,
    confirmFunc: string,
    cancelFunc?: string,
  ): Promise<void> {
    const cb = `((result)=>{
        if(result){
          ${confirmFunc}
        }else{
          ${cancelFunc ?? ""}
        }
      })`;
    await netCallNativeUi(NativeUI.OpenDialogConfirm, {
      config: JSON.stringify(config),
      cb,
    });
  }

  async openWarning(
    config: Dialogs.IConfirmConfig,
    confirmFunc: string,
    cancelFunc?: string,
  ): Promise<void> {
    const cb = `((result)=>{
        if(result){
          ${confirmFunc}
        }else{
          ${cancelFunc ?? ""}
        }
      })`;
    await netCallNativeUi(NativeUI.OpenDialogWarning, {
      config: JSON.stringify(config),
      cb,
    });
  }
}
