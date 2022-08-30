export namespace Icon {
  export enum IconType {
    NamedIcon = "NamedIcon",
    AssetIcon = "AssetIcon",
  }

  export interface IPlaocIcon {
    source: string;
    un_source?: string; // 未选中显示的图标， 一旦传递这个属性，关掉白背景选中
    type: IconType;
    description?: string;
    size?: number;
  }
}
