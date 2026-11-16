<script setup>
import AppIcon from "./AppIcon.vue";

const sections = [
  { label: "Overview", links: [{ to: "/", text: "Dashboard", icon: "dashboard" }] },
  {
    label: "Care",
    links: [
      { to: "/patients", text: "Patients", icon: "users" },
      { to: "/vaccinations", text: "Vaccinations", icon: "syringe" }
    ]
  },
  {
    label: "Logistics",
    links: [
      { to: "/vaccines", text: "Vaccine inventory", icon: "package" },
      { to: "/clinics", text: "Clinics", icon: "building" },
      { to: "/staff", text: "Clinical staff", icon: "stethoscope" }
    ]
  }
];
</script>

<template>
  <aside class="sidebar">
    <RouterLink class="brand" to="/">
      <span class="brand-mark" aria-hidden="true">
        <svg viewBox="0 0 32 32"><rect width="32" height="32" rx="9" /><path d="M13 8h6v5h5v6h-5v5h-6v-5H8v-6h5z" /></svg>
      </span>
      <span class="brand-text">
        <strong>Healthcare Ops</strong>
        <small>Operations dashboard</small>
      </span>
    </RouterLink>

    <nav class="nav" aria-label="Primary navigation">
      <template v-for="section in sections" :key="section.label">
        <p class="nav-label">{{ section.label }}</p>
        <RouterLink v-for="link in section.links" :key="link.to" :to="link.to" exact-active-class="active">
          <AppIcon :name="link.icon" /><span>{{ link.text }}</span>
        </RouterLink>
      </template>
    </nav>

    <div class="sidebar-footer">
      <a href="/api/health" class="sidebar-api" target="_blank" rel="noopener">
        <AppIcon name="code" /><span>REST API status</span>
      </a>
    </div>
  </aside>
</template>
