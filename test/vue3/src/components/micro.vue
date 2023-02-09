<script setup lang="ts">
import { IonButton, IonIcon } from '@ionic/vue';
import { defineComponent, onMounted } from 'vue';
defineComponent({
  components: { IonButton, IonIcon },
});


onMounted(() => {

})

async function onFatchNative(cmd: string) {
  const result = await fetch(`file://mwebview.sys.dweb/${cmd}`);
  console.log("vue#micro onFatchNative:", result)
}


</script>

<template>
  <h2>micro x fetch</h2>
  <ion-button color="tertiary" @click="onFatchNative('open?origin=https://www.baidu.com')">/open</ion-button>
  <ion-button color="tertiary" @click="onFatchNative('evalJavascript/KEJPMHLA')">/evalJavascript/KEJPMHLA</ion-button>
  <ion-button color="tertiary" @click="onFatchNative('listen/KEJPMHLA')">/listen/KEJPMHLA</ion-button>
  <ion-button color="tertiary" @click="onFatchNative('request/KEJPMHLA')">/request/KEJPMHLA</ion-button>
  <ion-button color="tertiary" @click="onFatchNative('response')">/response</ion-button>

</template>

<style scoped>

</style>
