/**
 * 格式化当前时间
 * @param now       当前日期
 * @param formatter 格式化格式
 * @returns
 */
export function format(now: Date | number, formatter = "YYYY-MM-DD hh:mm:ss") {
  if (typeof now === "number") {
    now = new Date(now);
  }

  // 普通的格式化
  const year = now.getFullYear();
  const month = now.getMonth() + 1;
  const day = now.getDate();
  const hour = now.getHours();
  const minute = now.getMinutes();
  const second = now.getSeconds();
  // 替换并返回格式化后的值
  formatter = formatter
    .replace("YYYY", `${year}`)
    .replace("MM", String(month)[1] ? `${month}` : `0${month}`)
    .replace("DD", String(day)[1] ? `${day}` : `0${day}`)
    .replace("hh", String(hour)[1] ? `${hour}` : `0${hour}`)
    .replace("mm", String(minute)[1] ? `${minute}` : `0${minute}`)
    .replace("ss", String(second)[1] ? `${second}` : `0${second}`);

  return formatter;
}
