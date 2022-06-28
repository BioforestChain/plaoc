declare namespace Plaoc {
  type DataString<T> = string;
  type ARGB = string; // #ffaa0000

  interface IColor {
    color: string;
    alpha: number;
  }
}
