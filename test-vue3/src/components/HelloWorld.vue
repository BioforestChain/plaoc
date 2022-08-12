<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { OpenScanner,DWebMessager} from '@bfsx/plugin';
import '@bfsx/plugin';
defineProps<{ msg: string }>()

function getToastMessage(data:string) {
  console.log("getToastMessage:",data)
  return document.getElementById('toastMessage')?.innerHTML;
}
let scannerData = ref("扫码返回的数据");
let dwebPluginData = ref("dweb的数据");
onMounted(async () => {
})

async function openScanner() {
const scanner = document.querySelector<OpenScanner>('dweb-scanner')!;
  const iter = await scanner.openScanner()
  console.log("scannerData.value = await scanner.openScanner() -->",JSON.stringify(iter))
  scannerData.value = iter
}
async function onDwebPlugin() {
   const dwebPlugin = document.querySelector<DWebMessager>('dweb-messager')!;
  // const iter = await dwebPlugin
  // console.log("document.querySelector('dweb-plugin') as dwebPlugin -->",JSON.stringify(iter))
  // dwebPluginData.value = iter
}

</script>
<template>
<dweb-messager id="dweb"></dweb-messager>
<dweb-scanner channelId="helloWorld"></dweb-scanner>


  <h1>{{ msg }}</h1>

  <div class="card">
    <button type="button" @click="openScanner">启动扫码</button>
    <p>
     {{scannerData}}
    </p>
  </div>
    <div class="card">
    <button type="button" @click="onDwebPlugin">webPlugin消息</button>
    <p>
     {{dwebPluginData}}
    </p>
  </div>
  <div style="margin-top: 50px;">
        <input id="toastMessage" type="text" placeholder="Toast message"/>
    </div>
  <p>
    Check out
    <a href="https://vuejs.org/guide/quick-start.html#local" target="_blank"
      >create-vue</a
    >, the official Vue + Vite starter
  </p>
  <p>
    Install
    <a href="https://github.com/johnsoncodehk/volar" target="_blank">Volar</a>
    in your IDE for a better DX
  </p>
  <p class="read-the-docs">Click on the Vite and Vue logos to learn more</p>
</template>

<style scoped>
.read-the-docs {
  color: #888;
}
</style>
