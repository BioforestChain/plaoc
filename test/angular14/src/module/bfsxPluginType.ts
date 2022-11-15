export namespace TopBar {
  export interface DwebTopBar {
    title: string;
    "background-color"?: Color.RGBAHex;
    "foreground-color"?: Color.RGBAHex;
    overlay?: string | boolean;
    hidden?: string | boolean;
  }

  export interface DwebTopBarButton {
    disabled?: string | boolean;
  }
}

export namespace BottomBar {
  export interface DwebBottomBar {
    hidden?: string | boolean;
    "background-color"?: Color.RGBAHex;
    "foreground-color"?: Color.RGBAHex;
    overlay?: string | boolean;
    height?: string;
  }

  export interface DwebBottomBarButton {
    disabled?: string | boolean;
    selected?: string | boolean;
    diSelectable?: string | boolean;
    "indicator-color"?: Color.RGBAHex;
  }

  export interface DwebBottomBarIcon {
    source: string;
    type?: Icon.IconType;
    description?: string;
    size?: string;
    color?: Color.RGBAHex;
    "selected-color"?: Color.RGBAHex;
  }

  export interface DwebBottomBarText {
    value: string;
    color?: Color.RGBAHex;
    "selected-color"?: Color.RGBAHex;
    "hide-value"?: Color.RGBAHex;
  }
}

export namespace Dialogs {
  interface DwebDialogsBase {
    title: string;
    disOnBackPress?: string | boolean;
    disOnClickOutside?: string | boolean;
  }
  export interface DwebDialogsAlert extends DwebDialogsBase {
    visible: string | boolean;
    content: string;
  }
  export interface DwebDialogsPrompt extends DwebDialogsBase {
    visible: string | boolean;
    label: string;
    defaultValue?: string;
  }
  export interface DwebDialogsConfirm extends DwebDialogsBase {
    visible: string | boolean;
    message: string;
  }
}

export namespace Icon {
  export type IconType = "NamedIcon" | "AssetIcon";
  export interface IPlaocIcon {
    source: string;
    type?: IconType;
    description?: string;
    size?: number;
  }
}

export namespace Keyboard {
  export interface DwebKeyboard {
    overlay?: string | boolean;
    hidden?: string | boolean;
  }
}

export namespace StatusBar {
  export enum StatusBarStyle {
    DEFAULT = "default",
    LIGHT_CONTENT = "light-content",
    DARK_CONTENT = "dark-content",
  }
  export interface DwebStatusBar {
    overlay?: string | boolean;
    hidden?: string | boolean;
    "bar-style"?: StatusBarStyle;
    "background-color"?: Color.RGBAHex;
  }
}

export namespace Color {
  export type RGBAHex = `#${string}`; // #ff000000 | #e0fa
}
