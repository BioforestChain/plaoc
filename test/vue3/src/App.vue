<script setup lang="ts">
// This starter template is using Vue 3 <script setup> SFCs
// Check out https://vuejs.org/api/sfc-script-setup.html#script-setup
import HelloWorld from './components/HelloWorld.vue'
import System_api from './view/system_api.vue';
import Dialogs from './view/dialogs.vue';
import { ref, defineComponent, reactive } from 'vue';
import { OpenScanner } from '@bfsx/plugin';
import { IonFab, IonFabButton, IonFabList,IonIcon } from '@ionic/vue';
import { 
  cartOutline,
  logoFacebook, 
  logoTwitter, 
  logoVimeo, 
} from 'ionicons/icons';

defineComponent({
  components: { IonFab, IonFabButton, IonFabList,IonIcon }
});

let scannerData = ref("DwebView-js ♻️ Deno-js");

async function openQrScanner() {
const scanner = document.querySelector<OpenScanner>('dweb-scanner')!;
  const iter = await scanner.openQrCodeScanner()
  console.log("scannerData.value = await scanner.openQrCodeScanner() -->",JSON.stringify(iter))
  scannerData.value = iter
}

async function openBarScanner() {
   const scanner = document.querySelector<OpenScanner>('dweb-scanner')!;
  const iter = await scanner.openBarCodeScanner()
  console.log("scannerData.value = await scanner.openBarCodeScanner() -->",JSON.stringify(iter))
  scannerData.value = iter
}

function pop() {
  console.log("冒泡")
}

const bottomBarImg = reactive({
  one: "https://objectjson.waterbang.top/test-vue3/land.svg",
  two: "https://objectjson.waterbang.top/test-vue3/scanner.svg",
  three: "https://objectjson.waterbang.top/test-vue3/home-not.svg"
})

function onBottomBar(serial:string) {
  bottomBarImg.one = 'https://objectjson.waterbang.top/test-vue3/land-not.svg'
  bottomBarImg.three = 'https://objectjson.waterbang.top/test-vue3/home-not.svg'
  if (serial=== 'one') {
    bottomBarImg[serial] = "https://objectjson.waterbang.top/test-vue3/land.svg"
  }
  if (serial=== 'three') {
     bottomBarImg[serial] = "https://objectjson.waterbang.top/test-vue3/home.svg"
  }
}

</script>

<template>
  <div class="topbar"></div>
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

  <dweb-top-bar id="topbar" title="Ar 扫雷" background-color="#eeee" foreground-color="#000" overlay="0.4">

    <dweb-top-bar-button id="aaa" @click="openBarScanner">
      <dweb-icon source="Filled.AddCircle"></dweb-icon>
    </dweb-top-bar-button>
    <dweb-top-bar-button id="ccc">
      <dweb-icon source="https://objectjson.waterbang.top/test-vue3/vite.svg" type="AssetIcon"></dweb-icon>
    </dweb-top-bar-button>
  </dweb-top-bar>
  <HelloWorld :msg="scannerData" />
  <div>
    <a href="https://vuejs.org/" target="_blank">
      <img src="./assets/waterbang.png" class="logo vue" alt="Vue logo" />
    </a>
    <a href="https://vuejs.org/" target="_blank">
      <img src="./assets/waterbang.png" class="logo vue" alt="Vue logo" />
    </a>
    <a href="https://vuejs.org/" target="_blank">
      <img src="./assets/waterbang.png" class="logo vue" alt="Vue logo" />
    </a>
  </div>
  <Dialogs></Dialogs>
  <Icon></Icon>
  <System_api></System_api>

  <dweb-bottom-bar id="bottombar" background-color="#D0BCFF" foreground-color="#1C1B1F" height="70" overlay="0.2">
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
  <div class="bottombar"></div>
</template>

<style scoped>
.logo {
  height: 6em;
  padding: 1.5em;
  will-change: filter;
}

.logo:hover {
  filter: drop-shadow(0 0 2em #646cffaa);
}

.logo.vue:hover {
  filter: drop-shadow(0 0 2em #42b883aa);
}

.topbar {
  margin-bottom: 80px;
}

.bottombar {
  margin-top: 80px;
}
</style>
