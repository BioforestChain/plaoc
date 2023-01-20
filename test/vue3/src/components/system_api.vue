<script setup lang="ts">
import { IonFab, IonFabButton, IonFabList, IonButton, IonIcon } from '@ionic/vue';
import { defineComponent, onMounted } from 'vue';
import { BfcsKeyboard, Navigation, BfcsStatusBar, App, DwebCamera, CameraDirection, CameraResultType, CameraSource, ImpactStyle, NotificationType, VibratePresetType, OpenScanner } from '@bfsx/plugin';

defineComponent({
  components: { IonFab, IonFabButton, IonFabList, IonButton, IonIcon }
});
let nav: Navigation;
onMounted(async () => {
  // 监听app消息
  listenerAppMessage()
  nav = document.querySelector<Navigation>('dweb-navigation')!
})

async function onShowKeyboard() {
  const keyboard = document.getElementById('key_board') as BfcsKeyboard
  keyboard?.showKeyboard()
  console.log("getKeyboardSafeArea:", JSON.stringify(await keyboard.getKeyboardSafeArea()))
  console.log("getKeyboardHeight:", await keyboard.getKeyboardHeight())
  console.log("getKeyboardOverlay:", await keyboard.getKeyboardOverlay())
}

function hideNavigation() {
  nav.setNavigationBarVisible(false)
}
async function getNavigationVisible() {
  console.log("getNavigationBarVisible=>", await nav.getNavigationBarVisible())
  console.log("getNavigationBarOverlay=>", await nav.getNavigationBarOverlay())
}
function setNavigationBarColor() {
  nav.setNavigationBarColor("#ffb94f", true, false)
}
function setNavigationBarOverlay() {
  nav.setNavigationBarOverlay(true)
}
function getStatusBarColor() {
  const status = document.querySelector<BfcsStatusBar>("dweb-status-bar");
  status?.getStatusBarBackgroundColor()
}
// app相关
function listenerAppMessage() {
  const app = document.querySelector<App>('dweb-app')!;
  app.listenBackButton((event) => {
    console.log("vue3:listenerAppMessage:", JSON.stringify(event))

  })
}
// 退出app
async function exitApp() {
  const app = document.querySelector<App>('dweb-app')!;
  app.exitApp()
}

// 复制到剪切板
async function writeClipboardContent() {
  const app = document.querySelector<App>("dweb-app")!;
  const bool = await app.writeClipboardContent({str: document.getElementById("title")!.textContent!});
  app.showToast(`复制${bool}`, "short", "top");
  const result = await app.readClipboardContent();
  app.showToast(`type: ${result.type} value: ${result.value}`, "long");
}

// 获取网络状态
async function getNetworkStatus() {
  const app = document.querySelector<App>("dweb-app")!;
  app.showToast(await app.getNetworkStatus(), "short");
}

// 拍摄照片
async function takeCameraPhoto() {
  const camera = document.querySelector<DwebCamera>("dweb-camera")!;
  const result = await camera.takeCameraPhoto({
    quality: 90,
    resultType: CameraResultType.Base64,
    allowEditing: false,
    saveToGallery: false,
    source: CameraSource.Camera,
    direction: CameraDirection.Rear
  });
  console.log("takeCameraPhoto result: ", result);
  console.log(JSON.parse(result as string));
}

// 从图库获取单张照片
async function pickCameraPhoto() {
  const camera = document.querySelector<DwebCamera>("dweb-camera")!;
  const result = await camera.pickCameraPhoto({
    quality: 80,
    allowEditing: false,
    resultType: CameraResultType.Uri,
    source: CameraSource.Photos
  });
  console.log("pickCameraPhoto result: ", result);
  console.log(JSON.parse(result as string));
}
// 从图库获取多张照片
async function pickCameraPhotos() {
  const camera = document.querySelector<DwebCamera>("dweb-camera")!;
  const result = await camera.pickCameraPhotos({
    quality: 100,
  });

  console.log("pickCameraPhotos result: ", result);
  console.log(JSON.parse(result as string));
}

async function hapticsImpact() {
  const app = document.querySelector<App>('dweb-app')!;
  await app.hapticsImpact({style: ImpactStyle.Light})
}
async function hapticsNotification() {
  const app = document.querySelector<App>('dweb-app')!;
  await app.hapticsNotification({type: NotificationType.Warning})
}
async function hapticsVibrate() {
  const app = document.querySelector<App>('dweb-app')!;
  await app.hapticsVibrate({duration: 1})
}
async function hapticsVibrateDisabled() {
  const app = document.querySelector<App>('dweb-app')!;
  // app.hapticsVibrate([1, 63, 1, 119, 1, 129, 1])
  await app.hapticsVibratePreset({type: VibratePresetType.DISABLED})
}
async function hapticsVibrateTick() {
  const app = document.querySelector<App>('dweb-app')!;
  // app.hapticsVibrate([10, 999, 1, 1])
  await app.hapticsVibratePreset({type: VibratePresetType.TICK})
}
async function hapticsVibrateDoubleClick() {
  const app = document.querySelector<App>('dweb-app')!;
  // app.hapticsVibrate([10, 1])
  await app.hapticsVibratePreset({type: VibratePresetType.DOUBLE_CLICK})
}
async function hapticsVibrateHeavyClick() {
  const app = document.querySelector<App>('dweb-app')!;
  // app.hapticsVibrate([1, 100, 1, 1])
  await app.hapticsVibratePreset({type: VibratePresetType.HEAVY_CLICK})
}

async function hapticsVibrateDisabledWeb() {
  if(window) {
    window.navigator.vibrate([1, 63, 1, 119, 1, 129, 1])
  } else if(globalThis) {
    globalThis.navigator.vibrate([1, 63, 1, 119, 1, 129, 1])
  }
  
}
async function hapticsVibrateTickWeb() {
  if(window) {
    window.navigator.vibrate([10, 999, 1, 1])
  } else if(globalThis) {
    globalThis.navigator.vibrate([10, 999, 1, 1])
  }
}
async function hapticsVibrateDoubleClickWeb() {
  if(window) {
    window.navigator.vibrate([10, 1])
  } else if(globalThis) {
    globalThis.navigator.vibrate([10, 1])
  }
}
async function hapticsVibrateHeavyClickWeb() {
  if(window) {
    window.navigator.vibrate([1, 100, 1, 1])
  } else if(globalThis) {
    globalThis.navigator.vibrate([1, 100, 1, 1])
  }
}
async function systemShare() {
  const app = document.querySelector<App>('dweb-app')!;
  await app.systemShare({title:"Ar扫雷", text: "hahahahaha"});
}
async function fileOpener() {
  const app = document.querySelector<App>("dweb-app")!;
  await app.fileOpener({filePath: "/嘎嘎.txt", openWithDefault: true})
}
async function toggleTorch() {
  const scanner = document.querySelector<OpenScanner>('dweb-scanner')!;
  await scanner.toggleTorch();
}
async function getTorchState() {
  const scanner = document.querySelector<OpenScanner>('dweb-scanner')!;
  const app = document.querySelector<App>("dweb-app")!;
  const result = await scanner.getTorchState();
  await app.showToast(result as string, "short");
}
</script>

<template>
  <dweb-app></dweb-app>
  <h2 id="title">andrid/ios 系统api 测试</h2>
  <dweb-status-bar id="status_bar" background-color="rgba(133,100,100,0.5)" bar-style="light-content"></dweb-status-bar>
  <dweb-keyboard id="key_board" hidden></dweb-keyboard>
  <dweb-navigation></dweb-navigation>
  <dweb-camera></dweb-camera>
  <ion-button expand="block" fill="outline" @click="exitApp">退出APP</ion-button>
  <ion-button expand="block" fill="outline" @click="hideNavigation">点我隐藏系统navigation</ion-button>
  <ion-button expand="block" fill="outline" @click="getNavigationVisible">获取navigation颜色</ion-button>
  <ion-button expand="block" fill="outline" @click="setNavigationBarOverlay">设置navigation透明</ion-button>
  <ion-button expand="block" fill="outline" @click="setNavigationBarColor">设置系统navigation颜色</ion-button>
  <ion-button expand="full" fill="outline" @click="onShowKeyboard">点击开启虚拟键盘</ion-button>
  <ion-button shape="round" fill="outline" @click="getStatusBarColor">点击获取状态栏颜色</ion-button>
  <ion-button shape="round" fill="outline" @click="writeClipboardContent">复制到剪切板</ion-button>
  <ion-button shape="round" fill="outline" @click="getNetworkStatus">获取网络状态</ion-button>
  <ion-button shape="round" fill="outline" @click="takeCameraPhoto">拍摄照片</ion-button>
  <ion-button shape="round" fill="outline" @click="pickCameraPhoto">单张照片</ion-button>
  <ion-button shape="round" fill="outline" @click="pickCameraPhotos">多张照片</ion-button>
  <ion-button expand="block" fill="outline" @click="hapticsImpact">触碰物体</ion-button>
  <ion-button expand="block" fill="outline" @click="hapticsNotification">振动通知</ion-button>
  <ion-button expand="block" fill="outline" @click="hapticsVibrate">反馈振动点击</ion-button>
  <ion-button shape="round" fill="outline" @click="hapticsVibrateDisabled">反馈振动禁用</ion-button>
  <ion-button shape="round" fill="outline" @click="hapticsVibrateDisabledWeb">反馈振动禁用web</ion-button>
  <ion-button shape="round" fill="outline" @click="hapticsVibrateTick">反馈振动滴答</ion-button>
  <ion-button shape="round" fill="outline" @click="hapticsVibrateTickWeb">反馈振动滴答web</ion-button>
  <ion-button shape="round" fill="outline" @click="hapticsVibrateDoubleClick">反馈振动双击</ion-button>
  <ion-button shape="round" fill="outline" @click="hapticsVibrateDoubleClickWeb">反馈振动双击web</ion-button>
  <ion-button shape="round" fill="outline" @click="hapticsVibrateHeavyClick">反馈振动重击</ion-button>
  <ion-button shape="round" fill="outline" @click="hapticsVibrateHeavyClickWeb">反馈振动重击web</ion-button>
  <ion-button expand="block" fill="outline" @click="systemShare">分享文案</ion-button>
  <ion-button expand="block" fill="outline" @click="fileOpener">打开文件</ion-button>
  <ion-button expand="block" fill="outline" @click="toggleTorch">打开/关闭手电筒</ion-button>
  <ion-button expand="block" fill="outline" @click="getTorchState">获取手电筒状态</ion-button>
  <div id="placeholder"></div>
</template>

<style scoped>
  #placeholder {
    height: 100px;
  }
</style>
