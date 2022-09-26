import { createRouter, createWebHistory } from '@ionic/vue-router';
import { RouteRecordRaw } from 'vue-router';
import system_api from "../view/system_api.vue"

const routes: Array<RouteRecordRaw> = [
  // routes go here
   { path: '/system_api',name: 'system_api', component: system_api }
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
