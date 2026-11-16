import { createRouter, createWebHistory } from "vue-router";
import DashboardView from "./views/DashboardView.vue";

export const routes = [
  { path: "/", name: "dashboard", component: DashboardView, meta: { title: "Dashboard" } },
  { path: "/patients", name: "patients", component: () => import("./views/PatientsView.vue"), meta: { title: "Patients" } },
  { path: "/vaccinations", name: "vaccinations", component: () => import("./views/VaccinationsView.vue"), meta: { title: "Vaccinations" } },
  { path: "/vaccines", name: "vaccines", component: () => import("./views/VaccinesView.vue"), meta: { title: "Vaccine inventory" } },
  { path: "/clinics", name: "clinics", component: () => import("./views/ClinicsView.vue"), meta: { title: "Clinics" } },
  { path: "/staff", name: "staff", component: () => import("./views/StaffView.vue"), meta: { title: "Clinical staff" } },
  { path: "/:pathMatch(.*)*", name: "not-found", component: () => import("./views/NotFoundView.vue"), meta: { title: "Page not found" } }
];

export const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: () => ({ top: 0 })
});

router.afterEach((to) => {
  document.title = `${to.meta.title ?? "HealthOps"} · HealthOps`;
});
