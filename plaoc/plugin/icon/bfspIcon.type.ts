export namespace Icon {
  export enum IconType {
    NamedIcon = "NamedIcon",
    AssetIcon = "AssetIcon",
  }

  export interface IPlaocIcon {
    source: string;
    type: IconType;
    description?: string;
    size?: number;
  }
}
