<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { DWebMessager} from '@bfsx/plugin';
defineProps<{ msg: string }>()

function getToastMessage(data:string) {
  console.log("getToastMessage:",data)
  return document.getElementById('toastMessage')?.innerHTML;
}
let dwebPluginData = ref("dweb的数据");
onMounted(async () => {
  console.log("document.querySelector('dweb-status-bar')!=>", document.querySelector('dweb-status-bar'));
})


async function onDwebPlugin() {
   const dwebPlugin = document.querySelector<DWebMessager>('dweb-messager')!;
   console.log("dwebPlugin:",dwebPlugin);
  // const iter = await dwebPlugin
  // console.log("document.querySelector('dweb-plugin') as dwebPlugin -->",JSON.stringify(iter))
  // dwebPluginData.value = iter
}

</script>
<template>
  <dweb-status-bar id="status_bar" background-color="#EADDFF" bar-style="dark-content"></dweb-status-bar>
  <!-- <dweb-keyboard id="key_board" overlay></dweb-keyboard>
    <dweb-dialog-alert id="dda" title="alert" content="content">
        <dweb-dialog-button id="ddb">ok</dweb-dialog-button>
    </dweb-dialog-alert>
   -->
  <dweb-messager id="dweb"></dweb-messager>
  <h1>{{ msg }}</h1>
  <div class="card">
    <button type="button" @click="onDwebPlugin">webMessage消息</button>
    <p>
      {{dwebPluginData}}
    </p>
  </div>
  <div>
    <input id="toastMessage" type="text" placeholder="Toast message" />
  </div>
  <p>
    Check out
    <a href="https://vuejs.org/guide/quick-start.html#local" target="_blank">create-vue</a>, the official Vue + Vite starter
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
