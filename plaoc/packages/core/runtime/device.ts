import { callDeno } from "../deno/android.fn.ts";
import { netCallNativeService } from '@bfsx/gateway';
import { js_to_rust_buffer, loopRustString } from "../deno/rust.op.ts";

const versionView = new Uint8Array([1]);
const headView = new Uint8Array([0, 0]);

export const isAndroid = await isDenoRuntime()

/**获取设备信息 */
export async function getDeviceInfo(): Promise<IDeviceInfo> {
  let info = ""
  try {
    info = await asyncCallNativeFunction(callDeno.getDeviceInfo);
  } catch (e) {
    console.log("device:", e)
    info = await netCallNativeService(callDeno.getDeviceInfo)
  }
  return JSON.parse(info)
}

/**判断是不是denoRuntime环境 */
export async function isDenoRuntime() {
  const info = await getDeviceInfo()
  console.log("是android 环境吗？", info.isDeno)
  return info.isDeno ? true : false
}


function asyncCallNativeFunction(handleFn: string, timeout = 3000): Promise<string> {
  // deno-lint-ignore no-async-promise-executor
  return new Promise(async (resolve, reject) => {
    const { headView } = await callNativeFunction(handleFn) // 发送请求
    do {
      const data = await loopRustString("op_rust_to_js_system_buffer").next();
      if (data.done) {
        continue;
      }
      // 如果请求是返回了是同一个表示头则返回成功
      const isCur = data!.headView.filter((byte, index) => {
        return byte === Array.from(headView)[index]
      })
      if (isCur.length === 2) {
        resolve(data.value);
        break;
      }
    } while (true);
    setTimeout(() => {
      reject("call function timeout")
    }, timeout);
  })
}
/**
 * 生成android还是ios的请求
 * @param handleFn
 * @param data
 */
function callNativeFunction(handleFn: string, data = "''") {
  const uint8Array = structureBinary(handleFn, data);
  // android - denoOp
  js_to_rust_buffer(uint8Array)
  return { versionView, headView }
}

function structureBinary(fn: string, data: string | Uint8Array = "") {
  const message = `{"function":"${fn}","data":${data}}`;

  // 字符 转 Uint8Array
  const encoder = new TextEncoder();
  const uint8Array = encoder.encode(message);

  return concatenate(versionView, headView, uint8Array);
}
/**
* 拼接Uint8Array
* @param arrays Uint8Array[]
* @returns Uint8Array
*/
function concatenate(...arrays: Uint8Array[]) {
  let totalLength = 0;
  for (const arr of arrays) {
    totalLength += arr.length;
  }
  const result = new Uint8Array(totalLength);
  let offset = 0;
  for (const arr of arrays) {
    result.set(arr, offset);
    offset += arr.length;
  }
  return result;
}

interface IDeviceInfo {
  name: string, // 设备名称 ios / android
  model: string, // 设备型号 ios / android
  modelName?: string, // 设备具体型号 ios
  systemVersion: string, // 版本号 ios / android
  localizedModel: string, // 设备区域化型号 ios
  processor?: string, // 处理器  android
  memory?: IMemoryData, // 运行内存 android
  storage?: IStorageSize, // 存储 android
  screen?: string, // 屏幕 android
  phone?: string, // 手机号码 android
  module: EDeviceModule, // 手机模式(silentMode,doNotDisturb,default) 
  isDeno?: boolean // android
}

export enum EDeviceModule {
  default = "default",
  silentMode = "silentMode",
  doNotDisturb = "doNotDisturb"
}

interface IMemoryData {
  total: string,
  usage: string,
  free: string,
  buffers: string
}

interface IStorageSize {
  hasExternalSDCard: boolean,
  internalTotalSize: string,
  internalFreeSize: string,
  externalTotalSize: string,
  externalFreelSize: string,
}
