<script setup lang="ts">
// This starter template is using Vue 3 <script setup> SFCs
// Check out https://vuejs.org/api/sfc-script-setup.html#script-setup

import { ref, defineComponent, reactive, onMounted, onBeforeMount } from 'vue';
import { IonFab, IonFabButton, IonFabList, IonIcon } from '@ionic/vue';
import {
  cartOutline,
  logoFacebook,
  logoTwitter,
  logoVimeo,
} from 'ionicons/icons';
import { BfcsTopBar, OpenScanner } from "@bfsx/plugin";
import { useRouter, RouterView } from 'vue-router';

defineComponent({
  components: { IonFab, IonFabButton, IonFabList, IonIcon }
});

const router = useRouter();

let scannerData = ref("DwebView-js ♻️ Deno-js");


onBeforeMount(() => {
  router.push({ path: `/`, query: { scannerData: `${scannerData.value}` } })
})


async function openQrScanner() {
  const scanner = document.querySelector<OpenScanner>('dweb-scanner')!;
  const iter = await scanner.openQrCodeScanner()
  console.log("scannerData.value = await scanner.openQrCodeScanner() -->", JSON.stringify(iter))
  scannerData.value = iter
}

async function openBarScanner() {
  const scanner = document.querySelector<OpenScanner>('dweb-scanner')!;
  const iter = await scanner.openBarCodeScanner()
  console.log("scannerData.value = await scanner.openBarCodeScanner() -->", JSON.stringify(iter))
  scannerData.value = iter
}

onMounted(async () => {
  const topBar = document.querySelector<BfcsTopBar>('dweb-top-bar')!;
  console.log("getTopBarShow: ", await topBar.getTopBarShow())
  console.log("getTopBarTitle: ", await topBar.getTopBarTitle())
  console.log("getTopBarHeight: ", await topBar.getTopBarHeight())
  console.log("getTopBarBackgroundColor: ", await topBar.getTopBarBackgroundColor())
  console.log("getTopBarForegroundColor: ", await topBar.getTopBarForegroundColor())
  console.log("getTopBarActions: ", await topBar.getTopBarActions())
})

function pop() {
  console.log("冒泡")
}

const bottomBarImg = reactive({
  one: "https://objectjson.waterbang.top/test-vue3/land.svg",
  two: "https://objectjson.waterbang.top/test-vue3/scanner.svg",
  three: "https://objectjson.waterbang.top/test-vue3/home-not.svg"
})

function onBottomBar(serial: string) {
  bottomBarImg.one = 'https://objectjson.waterbang.top/test-vue3/land-not.svg'
  bottomBarImg.three = 'https://objectjson.waterbang.top/test-vue3/home-not.svg'
  if (serial === 'one') {
    bottomBarImg[serial] = "https://objectjson.waterbang.top/test-vue3/land.svg"
  }
  if (serial === 'three') {
    bottomBarImg[serial] = "https://objectjson.waterbang.top/test-vue3/home.svg"
  }
}
getBlockInfo()
async function getBlockInfo() {
  fetch('/getBlockInfo', {
    headers: { 'Content-type': 'application/json' },
  }).then(res => res.json()).then(async (response) => {
    console.log("我是getBlockInfo：", JSON.stringify(response))
  }).catch((error) => {
    console.log('Looks like there was a problem: \n', error);
  });
}

</script>

<template>
  <div class="topbar">
    <ion-fab vertical="center" horizontal="end">
      <ion-fab-button size="small" @click="pop">
        <ion-icon :icon="cartOutline"></ion-icon>
      </ion-fab-button>
      <ion-fab-list>
        <ion-fab-button color="light">
          <ion-icon :icon="logoFacebook"></ion-icon>
        </ion-fab-button>
        <ion-fab-button color="light">
          <ion-icon :icon="logoTwitter"></ion-icon>
        </ion-fab-button>
        <ion-fab-button color="light">
          <ion-icon :icon="logoVimeo"></ion-icon>
        </ion-fab-button>
      </ion-fab-list>
    </ion-fab>
    <dweb-top-bar id="topbar" title="Ar 扫雷" background-color="#99CC33" foreground-color="#000" overlay>

      <dweb-top-bar-button id="aaa" @click="openBarScanner">
        <dweb-icon source="Filled.AddCircle"></dweb-icon>
      </dweb-top-bar-button>
      <dweb-top-bar-button id="ccc">
        <dweb-icon source="https://objectjson.waterbang.top/test-vue3/vite.svg" type="AssetIcon"></dweb-icon>
      </dweb-top-bar-button>
    </dweb-top-bar>
  </div>
  <RouterView />

  <div class="bottombar">
    <dweb-bottom-bar id="bottombar" background-color="#D0BCFF00" foreground-color="#1C1B1F00" height=70 overlay>
      <dweb-bottom-bar-button id="ddd" selected @click="onBottomBar('one')">
        <dweb-bottom-bar-icon :source="bottomBarImg.one" type="AssetIcon"></dweb-bottom-bar-icon>
        <dweb-bottom-bar-text color="#938F99" selected-color="#1C1B1F" value="土地"></dweb-bottom-bar-text>
      </dweb-bottom-bar-button>
      <dweb-bottom-bar-button id="eee" @click="openQrScanner" diSelectable>
        <dweb-bottom-bar-icon :source="bottomBarImg.two" type="AssetIcon"></dweb-bottom-bar-icon>
        <dweb-bottom-bar-text value="扫码"></dweb-bottom-bar-text>
      </dweb-bottom-bar-button>
      <dweb-bottom-bar-button id="fff" @click="onBottomBar('three')">
        <dweb-bottom-bar-icon :source="bottomBarImg.three" type="AssetIcon"></dweb-bottom-bar-icon>
        <dweb-bottom-bar-text color="#938F99" selected-color="#1C1B1F" value="个人空间"></dweb-bottom-bar-text>
      </dweb-bottom-bar-button>
    </dweb-bottom-bar>
    <dweb-scanner></dweb-scanner>
  </div>
</template>

<style scoped>
</style>
