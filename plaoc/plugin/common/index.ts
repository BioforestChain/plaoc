/**
 * 等待函数
 * @param delay 
 * @returns 
 */
export const loop = (delay: number) =>
  new Promise((resolve) => setTimeout(resolve, delay));


export function preload(baseModule: any) {
  var scriptElement = document.createElement('script');
  document.body.appendChild(scriptElement);
  scriptElement.src = baseModule
};
