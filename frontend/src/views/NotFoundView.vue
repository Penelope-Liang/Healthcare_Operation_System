<script setup>
import AppIcon from "@/components/AppIcon.vue";
</script>

<template>
  <div class="content">
    <section class="card status-card">
      <span class="icon-bubble warning"><AppIcon name="alert" /></span>
      <h1>Page not found</h1>
      <p>The page you’re looking for doesn’t exist.</p>
      <div><RouterLink class="btn btn-primary" to="/">Back to dashboard</RouterLink></div>
    </section>
  </div>
</template>
