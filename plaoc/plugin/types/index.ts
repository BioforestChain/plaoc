import { Color } from "./colorType";

export function hex<T extends string>(s: Color.HexColor<T>): T {
  return s;
}
