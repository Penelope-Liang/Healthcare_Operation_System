<script setup>
import AppIcon from "./AppIcon.vue";

defineProps({
  loading: { type: Boolean, default: false },
  error: { type: Object, default: null }
});
defineEmits(["retry"]);
</script>

<template>
  <section v-if="error" class="card status-card" role="alert">
    <span class="icon-bubble warning"><AppIcon :name="error.databaseDown ? 'database' : 'alert'" /></span>
    <template v-if="error.databaseDown">
      <h2>Database is not connected</h2>
      <p>HealthOps couldn’t reach MySQL, so this page can’t load its records yet.</p>
      <ol>
        <li>Start MySQL and import <code>database/covidDB.sql</code>.</li>
        <li>Check the <code>DB_HOST</code>, <code>DB_USER</code> and <code>DB_PASSWORD</code> environment variables.</li>
        <li>Or run without MySQL: <code>mvn spring-boot:run -Dspring-boot.run.profiles=demo</code></li>
      </ol>
    </template>
    <template v-else>
      <h2>Something went wrong</h2>
      <p>{{ error.message }}</p>
    </template>
    <div><button type="button" class="btn btn-primary" @click="$emit('retry')">Try again</button></div>
  </section>
  <div v-else-if="loading" class="card empty" aria-busy="true">Loading…</div>
  <slot v-else />
</template>
