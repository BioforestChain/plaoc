declare namespace Plaoc {
  type DataString<T> = string;

  type HexDigit =
    | "0"
    | "1"
    | "2"
    | "3"
    | "4"
    | "5"
    | "6"
    | "7"
    | "8"
    | "9"
    | "a"
    | "b"
    | "c"
    | "d"
    | "e"
    | "f"
    | "A"
    | "B"
    | "C"
    | "D"
    | "E"
    | "F";

  type HexColor<T extends string> =
    T extends `#${HexDigit}${HexDigit}${HexDigit}${infer Rest1}`
      ? Rest1 extends ``
        ? T // three-digit hex color
        : Rest1 extends `${HexDigit}${HexDigit}${HexDigit}`
        ? T // six-digit hex color
        : never
      : never;

  type RGBAHex = string;
  type ARGB = string;
  type RGB = string; // #ff0000
  type AlphaValueHex = `${HexDigit}${HexDigit}`;

  interface IColor {
    color: RGB;
    alpha: number;
  }
}
