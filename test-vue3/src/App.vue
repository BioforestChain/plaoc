<script setup lang="ts">
// This starter template is using Vue 3 <script setup> SFCs
// Check out https://vuejs.org/api/sfc-script-setup.html#script-setup
import HelloWorld from './components/HelloWorld.vue'
import { OpenScanner } from '@bfsx/plugin';
import { ref } from 'vue';
let scannerData = ref("DwebView-js ♻️ Deno-js");
async function openScanner() {
const scanner = document.querySelector<OpenScanner>('dweb-scanner')!;
  const iter = await scanner.openScanner()
  console.log("scannerData.value = await scanner.openScanner() -->",JSON.stringify(iter))
  scannerData.value = iter
}

</script>

<template>
    <dweb-top-bar id="topbar" title="Ar 扫雷" background-color="#000"  overlay="0.4" >
        <dweb-top-bar-button id="aaa">
            <dweb-icon source="Filled.AddCircle"></dweb-icon>
        </dweb-top-bar-button>
        <dweb-top-bar-button id="ccc">
            <dweb-icon source="https://objectjson.waterbang.top/test-vue3/vite.svg" type="AssetIcon"></dweb-icon>
        </dweb-top-bar-button>
    </dweb-top-bar>
  <div>
    <a href="https://vitejs.dev" target="_blank">
      <img src="/vite.svg" class="logo" alt="Vite logo" />
    </a>
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
  <HelloWorld :msg="scannerData" />
     <dweb-bottom-bar id="bottom_bar" background-color="#D0BCFF00" foreground-color="#1C1B1F"  height="70"  overlay="0.2" >
        <dweb-bottom-bar-button id="ddd" selected   >
            <dweb-bottom-bar-icon source="https://objectjson.waterbang.top/test-vue3/land.svg" un-source="https://objectjson.waterbang.top/test-vue3/land-not.svg"  type="AssetIcon"   ></dweb-bottom-bar-icon>
            <dweb-bottom-bar-text color="#938F99" selected-color="#1C1B1F"  value="土地" hide-value ></dweb-bottom-bar-text>
        </dweb-bottom-bar-button>
        <dweb-bottom-bar-button id="eee"  @click="openScanner" diSelectable>
            <dweb-bottom-bar-icon source="https://objectjson.waterbang.top/test-vue3/scanner.svg" type="AssetIcon"></dweb-bottom-bar-icon>
            <dweb-bottom-bar-text  value="扫码" ></dweb-bottom-bar-text>
        </dweb-bottom-bar-button>
        <dweb-bottom-bar-button id="fff" indicator-color="rgba(208,188,255,0)" >
            <dweb-bottom-bar-icon source="https://objectjson.waterbang.top/test-vue3/home.svg" un-source="https://objectjson.waterbang.top/test-vue3/home-not.svg" type="AssetIcon"  ></dweb-bottom-bar-icon>
            <dweb-bottom-bar-text color="#938F99" selected-color="#1C1B1F" value="个人空间" hide-value  ></dweb-bottom-bar-text>
        </dweb-bottom-bar-button>
    </dweb-bottom-bar>
<dweb-scanner channelId="helloWorld"></dweb-scanner>
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
</style>
