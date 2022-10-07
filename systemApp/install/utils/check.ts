/**
 * 统一社会信用代码校验规则
 * 不用I、O、S、V、Z
 * i混1、o混0、s混5、v混u，z混2
 */
const STR = "0123456789ABCDEFGHJKLMNPQRTUWXY";
// const codeMap = new Map<number, string>([
//   [0, "0"],
//   [1, "1"],
//   [2, "2"],
//   [3, "3"],
//   [4, "4"],
//   [5, "5"],
//   [6, "6"],
//   [7, "7"],
//   [8, "8"],
//   [9, "9"],
//   [10, "A"],
//   [11, "B"],
//   [12, "C"],
//   [13, "D"],
//   [14, "E"],
//   [15, "F"],
//   [16, "G"],
//   [17, "H"],
//   [18, "J"],
//   [19, "K"],
//   [20, "L"],
//   [21, "M"],
//   [22, "N"],
//   [23, "P"],
//   [24, "Q"],
//   [25, "R"],
//   [26, "T"],
//   [27, "U"],
//   [28, "W"],
//   [29, "X"],
//   [30, "Y"],
// ]);
// 权重值
const WEIGHTED_FACTORS = [1, 3, 9, 27, 19, 26, 16];

/**
 * 检验校验位是否合法
 * @param bfsAppId 应用id
 * @returns
 */
export function checkSign(bfsAppId: string): boolean {
  return new Promise<boolean>((resolve) => {
    let flags = false;
    if (bfsAppId.length !== 8) {
      return resolve && resolve(flags);
    }

    let total = 0;
    const strArray = STR.split("");
    const bfsArray = bfsAppId.slice(0, 7).split("");
    const signAt = bfsAppId[-1];
    for (let i = 0; i < bfsArray.length; i++) {
      if (!strArray.includes(bfsArray[i])) {
        break;
      }

      total = total + WEIGHTED_FACTORS[i] * STR.indexOf(bfsArray[i]);
    }

    const result = 31 - (total % 31);

    if (result === STR.indexOf(signAt)) {
      flags = true;
    }

    return resolve && resolve(flags);
  });
}

/**
 * 计算校验位值
 * @param checkStr 生成的7位码
 * @returns
 */
export function countCheckAt(checkStr: string): string {
  let total = 0;
  const checkArray = checkStr.split("");
  for (let i = 0; i < checkArray.length; i++) {
    total = total + WEIGHTED_FACTORS[i] * STR.indexOf(checkArray[i]);
  }

  const result = 31 - (total % 31);
  return STR[result];
}

/**
 * 生成bfsAppId
 * @returns
 */
export function genBfsAppId(): Promise<string> {
  return new Promise<string>((resolve) => {
    let checkStr = "";

    for (var i = 0; i < n; i++) {
      var id = Math.ceil(Math.random() * 30);
      checkStr += STR[id];
    }

    const checkAt = countCheckAt(checkStr);

    return resolve && resolve(checkStr + checkAt);
  });
}
