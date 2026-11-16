import { fileURLToPath, URL } from "node:url";
import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: { "@": fileURLToPath(new URL("./src", import.meta.url)) }
  },
  server: {
    port: 5173,
    // During `npm run dev` the Spring Boot API runs separately on :8080.
    proxy: { "/api": "http://localhost:8080" }
  },
  build: {
    // Maven packages everything in target/classes/static into the Spring Boot jar.
    outDir: "../target/classes/static",
    emptyOutDir: true
  },
  test: {
    environment: "jsdom"
  }
});
