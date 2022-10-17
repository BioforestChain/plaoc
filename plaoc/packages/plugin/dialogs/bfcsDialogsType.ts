import { Data } from "../types/dataType.ts";

export namespace Dialogs {
  export interface IBaseConfig {
    title: string;
    confirmText: string;
    dismissOnBackPress?: boolean;
    dismissOnClickOutside?: boolean;
  }

  export interface IAlertConfig extends IBaseConfig {
    content: string;
  }

  export interface IPromptConfig extends IBaseConfig {
    label: string;
    cancelText?: string;
    defaultValue?: string;
  }

  export interface IConfirmConfig extends IBaseConfig {
    message: string;
    cancelText?: string;
  }

  export interface IAlertConfigIOS extends IAlertConfig {
    confirmFunc: string;
  }

  export interface IPromptConfigIOS extends IPromptConfig {
    confirmFunc: string;
    cancelFunc?: string;
  }

  export interface IConfirmConfigIOS extends IConfirmConfig {
    confirmFunc: string;
    cancelFunc?: string;
  }

  export interface DialogsAndroidFFI {
    openAlert(config: Data.DataString<IAlertConfig>, cb: string): void;
    openPrompt(config: Data.DataString<IPromptConfig>, cb: string): void;
    openConfirm(config: Data.DataString<IConfirmConfig>, cb: string): void;
    openWarning(config: Data.DataString<IConfirmConfig>, cb: string): void;
  }

  export interface DialogsIosFFI {
    openAlert: {
      postMessage(config: Data.DataString<IAlertConfigIOS>): void;
    };
    openPrompt: {
      postMessage(config: Data.DataString<IPromptConfigIOS>): void;
    };
    openConfirm: {
      postMessage(config: Data.DataString<IConfirmConfigIOS>): void;
    };
    openWarning: {
      postMessage(config: Data.DataString<IConfirmConfigIOS>): void;
    };
  }

  export interface IDialogsNet {
    openAlert(config: IAlertConfig, confirmFunc: string): Promise<void>;
    openPrompt(
      config: IPromptConfig,
      confirmFunc: string,
      cancelFunc?: string,
    ): Promise<void>;
    openConfirm(
      config: IConfirmConfig,
      confirmFunc: string,
      cancelFunc?: string,
    ): Promise<void>;
    openWarning(
      config: IConfirmConfig,
      confirmFunc: string,
      cancelFunc?: string,
    ): Promise<void>;
  }
}
