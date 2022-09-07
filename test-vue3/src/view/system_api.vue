<script setup lang="ts">
import { IonFab, IonFabButton, IonFabList,IonButton,IonIcon} from '@ionic/vue';
import { defineComponent, onMounted } from 'vue';
import { BfcsKeyboard } from '@bfsx/plugin';

defineComponent({
  components: { IonFab, IonFabButton, IonFabList,IonButton, IonIcon }
});

onMounted(async () => {
  console.log(" document.getElementById('key_board')=>",  JSON.stringify(document.querySelector('dweb-keyboard')));
})

async function onShowKeyboard() {
  const keyboard = document.getElementById('key_board') as BfcsKeyboard
  keyboard?.showKeyboard()
  console.log("getKeyboardSafeArea:", JSON.stringify(await keyboard.getKeyboardSafeArea()))
  console.log("getKeyboardHeight:",await keyboard.getKeyboardHeight())
  console.log("getKeyboardOverlay:",await keyboard.getKeyboardOverlay())
}

</script>

<template>
  <h2>andrid/ios 系统api 测试</h2>
  <dweb-status-bar id="status_bar" background-color="rgba(100,100,100,0.5)" overlay></dweb-status-bar>
  <dweb-keyboard id="key_board" hidden="true"></dweb-keyboard>
  <ion-button expand="block" fill="outline">点我隐藏系统navigation</ion-button>
  <ion-button expand="full" fill="outline" @click="onShowKeyboard">点击开启虚拟键盘</ion-button>
  <ion-button shape="round" fill="outline">Outline + Round</ion-button>
</template>

<style scoped>
</style>
