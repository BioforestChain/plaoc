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
  console.log("document.querySelector('dweb-status-bar')!=>", document.querySelector('dweb-status-bar'));
  console.log("dweb-top-bar-button =>", document.getElementById('aaa'));
})

async function openScanner() {
const scanner = document.querySelector<OpenScanner>('dweb-scanner')!;
  const iter = await scanner.openScanner()
  console.log("scannerData.value = await scanner.openScanner() -->",JSON.stringify(iter))
  scannerData.value = iter
}
async function onDwebPlugin() {
   const dwebPlugin = document.querySelector<DWebMessager>('dweb-messager')!;
   console.log("dwebPlugin:",dwebPlugin);
   console.log("_ffixxx:",JSON.stringify(Object.keys((window as any).bottom_bar)));
  // const iter = await dwebPlugin
  // console.log("document.querySelector('dweb-plugin') as dwebPlugin -->",JSON.stringify(iter))
  // dwebPluginData.value = iter
}

</script>
<template>
   <dweb-status-bar id="status_bar" background-color="#f71" bar-style="dark-content"></dweb-status-bar>
    <dweb-top-bar id="topbar" title="test" background-color="rgba(255, 255, 255)" foreground-color="#0000ffff">
        <dweb-top-bar-button id="aaa">
            <dweb-icon source="Filled.AddCircle"></dweb-icon>
        </dweb-top-bar-button>
        <dweb-top-bar-button id="bbb" disabled>
            <dweb-icon source="Filled.AddCircle" size="20"></dweb-icon>
        </dweb-top-bar-button>
        <dweb-top-bar-button id="ccc">
            <dweb-icon source="/vite.svg" type="AssetIcon"></dweb-icon>
        </dweb-top-bar-button>
    </dweb-top-bar>
    <!-- <dweb-keyboard id="key_board" overlay></dweb-keyboard>
    <dweb-dialog-alert id="dda" title="alert" content="content">
        <dweb-dialog-button id="ddb">ok</dweb-dialog-button>
    </dweb-dialog-alert>
    <dweb-dialog-prompt id="ddp" title="prompt" label="label" defaultValue="default">
        <dweb-dialog-button id="ddpo" aria-label="cancel">ok</dweb-dialog-button>
        <dweb-dialog-button id="ddpn" aria-label="confirm">No</dweb-dialog-button>
    </dweb-dialog-prompt>
    <dweb-dialog-confirm id="ddc" title="confirm" message="message">
        <dweb-dialog-button id="ok" aria-label="confirm">ok</dweb-dialog-button>
        <dweb-dialog-button id="no" aria-label="cancel">No</dweb-dialog-button>
    </dweb-dialog-confirm>
    <dweb-dialog-before-unload id="before_unload" title="test" message="before-unload">
        <dweb-dialog-button id="buo">ok</dweb-dialog-button>
        <dweb-dialog-button id="bun">No</dweb-dialog-button>
    </dweb-dialog-before-unload> -->
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
    <button type="button" @click="onDwebPlugin">webMessage消息</button>
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
