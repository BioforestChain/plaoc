<script setup lang="ts">
import { IonButton, IonIcon } from '@ionic/vue';
import { defineComponent, reactive } from 'vue';
defineComponent({
  components: { IonButton, IonIcon },
});

const visable = reactive({
  alert:false,
  prompt:false,
  confirm: false,
  warning: false
})
function onCheckVisable(call:string,bool:boolean) {
  console.log("onCheckVisable:",call,bool)
  visable[call] = bool
}
</script>

<template>
  <h2>dialogs</h2>
  <ion-button color="primary" @click="onCheckVisable('alert',true)">alert</ion-button>
  <ion-button color="secondary" @click="onCheckVisable('prompt',true)">prompt</ion-button>
  <ion-button color="tertiary" @click="onCheckVisable('confirm',true)">confirm</ion-button>
  <ion-button color="warning" @click="onCheckVisable('warning',true)">warning</ion-button>

  <dweb-dialog-alert :visible="visable.alert" title="810975" content="密码忘了怎么办？" disOnClickOutside disOnBackPress>
    <dweb-dialog-button @click="onCheckVisable('alert',false)">确定</dweb-dialog-button>
  </dweb-dialog-alert>
  <dweb-dialog-prompt :visible="visable.prompt" title="prompt" label="我是提示文案" defaultValue="扣1送火麒麟" disOnClickOutside disOnBackPress>
    <dweb-dialog-button @click="onCheckVisable('prompt',false)" aria-label="cancel">拒绝</dweb-dialog-button>
    <dweb-dialog-button @click="onCheckVisable('prompt',false)" aria-label="confirm">11111111</dweb-dialog-button>
  </dweb-dialog-prompt>
  <dweb-dialog-confirm :visible="visable.confirm" title="confirm" message="南水北调" disOnClickOutside disOnBackPress>
    <dweb-dialog-button @click="onCheckVisable('confirm',false)" aria-label="cancel">No</dweb-dialog-button>
    <dweb-dialog-button @click="onCheckVisable('confirm',false)" aria-label="confirm">ok</dweb-dialog-button>
  </dweb-dialog-confirm>
  <dweb-dialog-warning :visible="visable.warning" title="FBI warning" message="FBI open the door">
    <dweb-dialog-button @click="onCheckVisable('warning',false)" aria-label="cancel">No</dweb-dialog-button>
    <dweb-dialog-button @click="onCheckVisable('warning',false)" aria-label="confirm">ok</dweb-dialog-button>
  </dweb-dialog-warning>
</template>

<style scoped>
</style>
