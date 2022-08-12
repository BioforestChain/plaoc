declare namespace Plaoc {
  interface IBaseConfig {
    title: string;
    confirmText: string;
    dismissOnBackPress?: boolean;
    dismissOnClickOutSide?: boolean;
  }

  interface IAlertConfig extends IBaseConfig {
    content: string;
  }

  interface IPromptConfig extends IBaseConfig {
    label: string;
    cancelText?: string;
    defaultValue?: string;
  }

  interface IConfirmConfig extends IBaseConfig {
    message: string;
    cancelText?: string;
  }

  interface IAlertConfigIOS extends IAlertConfig {
    confirmFunc: string;
  }

  interface IPromptConfigIOS extends IPromptConfig {
    confirmFunc: string;
    cancelFunc?: string;
  }

  interface IConfirmConfigIOS extends IConfirmConfig {
    confirmFunc: string;
    cancelFunc?: string;
  }

  interface DialogsAndroidFFI {
    openAlert(config: Plaoc.DataString<IAlertConfig>, cb: string): void;
    openPrompt(config: Plaoc.DataString<IPromptConfig>, cb: string): void;
    openConfirm(config: Plaoc.DataString<IConfirmConfig>, cb: string): void;
    openBeforeUnload(
      config: Plaoc.DataString<IConfirmConfig>,
      cb: string
    ): void;
  }

  interface DialogsIosFFI {
    openAlert: {
      postMessage(config: Plaoc.DataString<IAlertConfigIOS>): void;
    };
    openPrompt: {
      postMessage(config: Plaoc.DataString<IPromptConfigIOS>): void;
    };
    openConfirm: {
      postMessage(config: Plaoc.DataString<IConfirmConfigIOS>): void;
    };
    openBeforeUnload: {
      postMessage(config: Plaoc.DataString<IConfirmConfigIOS>): void;
    };
  }

  interface IDialogsFFI {
    openAlert(config: IAlertConfig, confirmFunc: string): Promise<void>;
    openPrompt(
      config: IPromptConfig,
      confirmFunc: string,
      cancelFunc?: string
    ): Promise<void>;
    openConfirm(
      config: IConfirmConfig,
      confirmFunc: string,
      cancelFunc?: string
    ): Promise<void>;
    openBeforeUnload(
      config: IConfirmConfig,
      confirmFunc: string,
      cancelFunc?: string
    ): Promise<void>;
  }
}

declare const native_dialog: Plaoc.DialogsAndroidFFI;
