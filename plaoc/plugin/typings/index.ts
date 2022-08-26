import { Color } from "./types/color.type";

export function hex<T extends string>(s: Color.HexColor<T>): T {
  return s;
}
