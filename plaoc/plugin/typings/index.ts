import "./types/color.type";
import "./types/index.type";
export function hex<T extends string>(s: Plaoc.HexColor<T>): T {
  return s;
}
