import "./types/color.type";
import "./types/data.type";

export function hex<T extends string>(s: Color.HexColor<T>): T {
  return s;
}
