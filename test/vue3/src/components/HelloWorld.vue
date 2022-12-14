<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { DWebMessager } from '@bfsx/plugin';
defineProps<{ msg: string }>()

function getToastMessage(data: string) {
  console.log("getToastMessage:", data)
  return document.getElementById('toastMessage')?.innerHTML;
}
let dwebPluginData = ref("dweb的数据");
onMounted(async () => {
})

async function onPushFile(e: Event) {
  const dwebPlugin = document.querySelector<DWebMessager>('dweb-messager')!;
  console.log("dwebPlugin:", dwebPlugin);
  const target = e.target as HTMLInputElement
  const files = Array.from(target.files!)// 注意这里取得的是一个类数组
  if (files) {
    // 取得文件
    const uploadedFile = files[0]

    const formData = new FormData()
    formData.append(uploadedFile.name, uploadedFile)
    console.log("formData", formData.get(uploadedFile.name))
    fetch('/upload', {
      body: formData,
      method: "post",
    }).then(async res => {
      console.log('哈哈哈哈哈哈', await res.text())
    }).catch(error => {
      console.log("error", error)
      // 文件上传失败
    }).finally(() => {
      // 文件上传完成，无论成功还是失败
      // 这里可以清除一下input.value
    })
  }
}

function InstallBFS() {
  (window as any).installBFS("https://shop.plaoc.com/KEJPMHLA/KEJPMHLA.zip")
}

</script>
<template>

  <h1>{{ msg }}</h1>
  <!-- 文件上传From形式 -->
  <ion-card>
    <ion-card-header>
      <ion-card-title>文件上传From形式</ion-card-title>
    </ion-card-header>
    <ion-card-content>
      <div> {{ dwebPluginData }} </div>
      <form class="from-data" method="post" enctype="multipart/from-data" action="api/upload">
        <input id="toastMessage" type="file" placeholder="Toast message" name="file">
        <ion-button type="submit">上传文件</ion-button>
      </form>
    </ion-card-content>
  </ion-card>
  <ion-card>
    <ion-card-header>
      <ion-card-title>文件上传FormData形式</ion-card-title>
    </ion-card-header>
    <ion-card-content class="file-input">
      <ion-input type="file" class="d-none" name="file" ref="uploadInput" @change="onPushFile" value="custom"></ion-input>
    </ion-card-content>
  </ion-card>
  <dweb-messager id="dweb"></dweb-messager>
  <p>
    Check out
    <a href="https://vuejs.org/guide/quick-start.html#local" target="_blank">create-vue</a>, the official Vue + Vite starter
  </p>
  <p>
    Install
    <a href="https://github.com/johnsoncodehk/volar" target="_blank">Volar</a>
    in your IDE for a better DX
  </p>
  <p class="read-the-docs">
    <ion-button expand="block" fill="outline" @click="InstallBFS()">下载应用</ion-button>
  </p>
</template>

<style scoped>
.read-the-docs {
  color: #888;
}

.from-data {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.file-input {
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
