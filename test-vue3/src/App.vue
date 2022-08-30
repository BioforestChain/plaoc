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
    <dweb-top-bar id="topbar" title="Ar 扫雷" background-color="rgba(255, 255, 255)" foreground-color="#0000ffff">
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
     <dweb-bottom-bar id="bottom_bar" background-color="#D0BCFFff"  >
        <dweb-bottom-bar-button id="ddd" selected   >
            <dweb-bottom-bar-icon source="https://objectjson.waterbang.top/test-vue3/land.svg" type="AssetIcon" ></dweb-bottom-bar-icon>
            <dweb-bottom-bar-text color="#332D41ff" selected-color="#381E72ff" value="土地"></dweb-bottom-bar-text>
        </dweb-bottom-bar-button>
        <dweb-bottom-bar-button id="eee"  @click="openScanner" diSelectable>
            <dweb-bottom-bar-icon source="https://objectjson.waterbang.top/test-vue3/scanner.svg" type="AssetIcon"></dweb-bottom-bar-icon>
            <dweb-bottom-bar-text color="#332D41ff" selected-color="#381E72ff" value="扫码"></dweb-bottom-bar-text>
        </dweb-bottom-bar-button>
        <dweb-bottom-bar-button id="fff" >
            <dweb-bottom-bar-icon source="https://objectjson.waterbang.top/test-vue3/home.svg" type="AssetIcon" ></dweb-bottom-bar-icon>
            <dweb-bottom-bar-text color="#332D41ff"  selected-color="#381E72ff" value="个人空间"></dweb-bottom-bar-text>
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
