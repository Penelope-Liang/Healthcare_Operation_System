<script setup>
import AppSidebar from "./components/AppSidebar.vue";
import ToastStack from "./components/ToastStack.vue";
</script>

<template>
  <div class="app">
    <AppSidebar />
    <main class="main">
      <RouterView />
    </main>
  </div>
  <ToastStack />
</template>
