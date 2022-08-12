declare namespace Plaoc {
  enum IconType {
    NamedIcon = "NamedIcon",
    AssetIcon = "AssetIcon",
  }

  interface IPlaocIcon {
    source: string;
    type: IconType;
    description?: string;
    size?: number;
  }
}
