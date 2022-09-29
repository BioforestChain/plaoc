/**
 * 等待函数
 * @param delay
 * @returns
 */
export const loop = (delay: number) =>
  new Promise((resolve) => setTimeout(resolve, delay));
