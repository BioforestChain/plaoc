<script setup lang="ts">
import { IonFab, IonFabButton, IonFabList,IonButton,IonIcon} from '@ionic/vue';
import { ref,defineComponent } from 'vue';

defineComponent({
  components: { IonFab, IonFabButton, IonFabList,IonButton, IonIcon }
});

</script>

<template>
  <h2>andrid/ios 系统api 测试</h2>
  <dweb-status-bar id="status_bar" background-color="rgba(100,100,100,0.5)" overlay></dweb-status-bar>
  <dweb-keyboard id="key_board" overlay></dweb-keyboard>
  <ion-button expand="block" fill="outline">点我隐藏系统navigation</ion-button>

</template>

<style scoped>
</style>
