<script setup lang="ts">
import { IonButton, IonIcon } from '@ionic/vue';
import { defineComponent, reactive, ref, onMounted } from 'vue';
defineComponent({
  components: { IonButton, IonIcon },
});

const dialogBack = ref("dialogs返回的数据📚")
const dialogPrompt = ref("扣1送火麒麟")

const visable = reactive({
  alert: false,
  prompt: false,
  confirm: false,
  warning: false
})

onMounted(() => {
  const prompt = document.getElementById("dialogPrompt")
  prompt?.addEventListener("click", (data: any) => {
    console.log("document.getElementById(dialogPrompt):", data.detail.result)
    dialogPrompt.value = data.detail.result
  })
})

function onCheckVisable(call: string, bool: boolean) {
  dialogBack.value = `点击了 ${call} dialog`
  console.log("onCheckVisable:", call, bool)
  visable[call] = bool
}
</script>

<template>
  <h2>dialogs</h2>
  <h3>{{dialogBack}}</h3>
  <h3>{{dialogPrompt}}</h3>
  <ion-button color="primary" @click="onCheckVisable('alert',true)">alert</ion-button>
  <ion-button color="secondary" @click="onCheckVisable('prompt',true)">prompt</ion-button>
  <ion-button color="tertiary" @click="onCheckVisable('confirm',true)">confirm</ion-button>
  <ion-button color="warning" @click="onCheckVisable('warning',true)">warning</ion-button>

  <dweb-dialog-alert :visible="visable.alert" title="810975" content="密码忘了怎么办？" disOnClickOutside disOnBackPress>
    <dweb-dialog-button @click="onCheckVisable('alert',false)">确定</dweb-dialog-button>
  </dweb-dialog-alert>
  <dweb-dialog-prompt :visible="visable.prompt" title="prompt" label="我是提示文案" :defaultValue="dialogPrompt" disOnClickOutside disOnBackPress>
    <dweb-dialog-button @click="onCheckVisable('prompt',false)" aria-label="cancel">拒绝</dweb-dialog-button>
    <dweb-dialog-button id="dialogPrompt" @click="onCheckVisable('prompt',false)" aria-label="confirm">11111111</dweb-dialog-button>
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
