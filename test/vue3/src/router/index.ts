import { createRouter, createWebHistory } from 'vue-router'
import homeView from "../view/main.vue"
import permissionsView from "../view/permissions.vue";


const routes = [
  {
    path: '/',
    name: 'home',
    component: homeView,
    props: route => ({ scannerData: route.query.scannerData })
  },
  {
    path: '/permissions',
    name: 'permissions',
    component: permissionsView
  },
  {
    name: '404',
    path: '/404',
    component: () => import('../view/404.vue')
  },
  {
    path: "/:catchAll(.*)",  // 此处需特别注意至于最底部
    redirect: '/404',
    meta: {
      requiresAuth: false
    }
  }
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

export default router;
