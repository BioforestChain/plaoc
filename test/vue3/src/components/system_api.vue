<script setup lang="ts">
import { IonFab, IonFabButton, IonFabList, IonButton, IonIcon } from '@ionic/vue';
import { defineComponent, onMounted } from 'vue';
import { BfcsKeyboard, Navigation, BfcsStatusBar, App } from '@bfsx/plugin';

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
</script>

<template>
  <dweb-app></dweb-app>
  <h2 id="title">andrid/ios 系统api 测试</h2>
  <dweb-status-bar id="status_bar" background-color="rgba(133,100,100,0.5)" bar-style="light-content"></dweb-status-bar>
  <dweb-keyboard id="key_board" hidden></dweb-keyboard>
  <dweb-navigation></dweb-navigation>
  <ion-button expand="block" fill="outline" @click="exitApp">退出APP</ion-button>
  <ion-button expand="block" fill="outline" @click="hideNavigation">点我隐藏系统navigation</ion-button>
  <ion-button expand="block" fill="outline" @click="getNavigationVisible">获取navigation颜色</ion-button>
  <ion-button expand="block" fill="outline" @click="setNavigationBarOverlay">设置navigation透明</ion-button>
  <ion-button expand="block" fill="outline" @click="setNavigationBarColor">设置系统navigation颜色</ion-button>
  <ion-button expand="full" fill="outline" @click="onShowKeyboard">点击开启虚拟键盘</ion-button>
  <ion-button shape="round" fill="outline" @click="getStatusBarColor">点击获取状态栏颜色</ion-button>
  <ion-button shape="round" fill="outline" @click="writeClipboardContent">复制到剪切板</ion-button>
  <ion-button shape="round" fill="outline" @click="getNetworkStatus">获取网络状态</ion-button>
  <div id="placeholder"></div>
</template>

<style scoped>
  #placeholder {
    height: 100px;
  }
</style>
