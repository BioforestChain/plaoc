<script setup lang="ts">
import HelloWorld from '../components/HelloWorld.vue'
import System_api from '../components/system_api.vue';
import Dialogs from '../components/dialogs.vue';
import { IonButton } from '@ionic/vue';
import { useRouter } from 'vue-router';
const router = useRouter();



function goPermissionView() {
  console.log("前往权限测试页面")
  router.push({ name: "permissions" })
}
defineProps<{ scannerData: string }>()

</script>
<template>
  <div class="main">
    <HelloWorld :msg="scannerData" />
    <div>
      <ion-button @click="goPermissionView()">前往权限测试页面</ion-button>
    </div>
    <div class="qr-code">
      <a href="https://vuejs.org/" target="_blank">
        <img src="../assets/waterbang.png" class="logo vue" alt="Vue logo" />
      </a>
      <a href="https://vuejs.org/" target="_blank">
        <img src="../assets/waterbang.png" class="logo vue" alt="Vue logo" />
      </a>
      <a href="https://vuejs.org/" target="_blank">
        <img src="../assets/waterbang.png" class="logo vue" alt="Vue logo" />
      </a>
    </div>
    <Dialogs></Dialogs>
    <System_api></System_api>
  </div>
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
