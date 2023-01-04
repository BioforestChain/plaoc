<script setup lang="ts">
import { IonButton } from '@ionic/vue';
import { onMounted } from 'vue';
import { Permissions, EPermissions } from '@bfsx/plugin';

let permission: Permissions
onMounted(() => {
  permission = document.querySelector<Permissions>('dweb-permission')!

})
/**
 * 检查是否有摄像头权限，如果没有或者被拒绝，那么会强制请求打开权限（设置）
 */
async function checkCameraPermission() {
  const res = await permission.checkCameraPermission()
  console.log("vue3#checkCameraPermission:", res)
}

/**
 * 申请多个权限
 * @param pers 
 */
async function applyPermissions(pers: EPermissions[]) {
  const res = await permission.applyPermissions(pers)
  console.log("vue3#checkCameraPermission:", res)
}
/**
 * 申请多个权限
 * @param pers 
 */
async function applyPermission(pers: EPermissions) {
  const res = await permission.applyPermission(pers)
  console.log("vue3#checkCameraPermission:", res)
}


</script>

<template>
  <dweb-permission></dweb-permission>
  <h2>权限申请 api 测试</h2>
  <ion-button expand="block" fill="outline" @click="checkCameraPermission">检测是否有相册权限</ion-button>
  <ion-button expand="block" fill="outline" @click="applyPermission(EPermissions.PHOTO)">申请相册权限</ion-button>
  <ion-button expand="block" fill="outline" @click="applyPermission(EPermissions.LOCATION)">申请位置权限</ion-button>
  <ion-button expand="block" fill="outline" @click="applyPermission(EPermissions.NETWORK)">申请网络权限</ion-button>
  <ion-button expand="block" fill="outline" @click="applyPermission(EPermissions.RECORD_AUDIO)">申请录音权限</ion-button>
  <ion-button expand="block" fill="outline" @click="applyPermission(EPermissions.MEDIA)">申请媒体库权限</ion-button>
  <ion-button expand="block" fill="outline" @click="applyPermission(EPermissions.CONTACTS)">申请联系人权限</ion-button>
  <ion-button expand="block" fill="outline" @click="applyPermission(EPermissions.NOTIFICATION)">申请通知权限</ion-button>
  <ion-button expand="block" fill="outline" @click="applyPermission(EPermissions.BLUETOOTH)">申请蓝牙权限</ion-button>
  <ion-button expand="block" fill="outline" @click="applyPermissions(
  [EPermissions.BODY_SENSORS, EPermissions.CALENDAR, EPermissions.STORAGE,
  EPermissions.CALL, EPermissions.DEVICE])">申请多个权限（传感器，日历，存储,电话，设备信息）</ion-button>
</template>

<style>

</style>
