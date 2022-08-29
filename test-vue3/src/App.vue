<script setup lang="ts">
// This starter template is using Vue 3 <script setup> SFCs
// Check out https://vuejs.org/api/sfc-script-setup.html#script-setup
import HelloWorld from './components/HelloWorld.vue'
import '@bfsx/plugin';



</script>

<template>
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
  <HelloWorld msg="DwebView-js ♻️ Deno-js" />
     <dweb-bottom-bar id="bottom_bar" background-color="#ff00ffff" foreground-color="#000000ff">
        <dweb-bottom-bar-button id="ddd" selectable indicator-color="#00ff00ff">
            <dweb-bottom-bar-icon source="/vite.svg" type="AssetIcon" color="#ff00aaff" selected-color="rgba(255, 0, 0, 1)"></dweb-bottom-bar-icon>
            <dweb-bottom-bar-text color="#0000ffff" selected-color="rgba(255, 0, 0, 1)">主页</dweb-bottom-bar-text>
        </dweb-bottom-bar-button>
        <dweb-bottom-bar-button id="eee">
            <dweb-bottom-bar-icon source="/vite.svg" type="AssetIcon" size="50"></dweb-bottom-bar-icon>
            <dweb-bottom-bar-text color="#0000ffff" selected-color="rgba(255, 0, 0, 1)">消息</dweb-bottom-bar-text>
        </dweb-bottom-bar-button>
        <dweb-bottom-bar-button id="fff" selected selectable>
            <dweb-bottom-bar-icon source="/vite.svg" type="AssetIcon" ></dweb-bottom-bar-icon>
            <dweb-bottom-bar-text>个人空间</dweb-bottom-bar-text>
        </dweb-bottom-bar-button>
    </dweb-bottom-bar>
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
