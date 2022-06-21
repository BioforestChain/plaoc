declare namespace Plaoc {
  type DialogsFFI = {
    openAlert(config: DataString<IAlertConfig>, cb: string): void;
    openPrompt(config: DataString<IPromptConfig>, cb: string): void;
    openConfirm(config: DataString<IConfirmConfig>, cb: string): void;
    openBeforeUnload(config: DataString<IConfirmConfig>, cb: string): void;
  };

  type DataString<T> = string;

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
}

declare const native_dialog: Plaoc.DialogsFFI;
