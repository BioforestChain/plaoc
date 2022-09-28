import { Runtime } from "./runtime.d.ts";

export class ScriptModule {
  constructor(_code: string, _url: string) { }
}

/**
 * 将一个函数转换为 blob，然后为这个 blob 生成 URL 对象
 * @param module {add:()=>{},egg:()=>{}}
 * @returns URL
 */
export const fnTransformURL = (module: Runtime.TModule_fn) => {
  const arrFn = Object.keys(module).map((fnName) => {
    return `const ${fnName} = ${module[fnName]}`;
  });
  console.log("arrFn", arrFn);
  const blob = new Blob(arrFn, {
    type: "application/javascript",
  });
  return URL.createObjectURL(blob);
};
