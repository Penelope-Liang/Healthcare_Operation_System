const { defineConfig, devices } = require("@playwright/test");

const port = process.env.PORT || "8080";
const baseURL = process.env.BASE_URL || `http://localhost:${port}`;

// By default the tests start the packaged app with the in-memory "demo" database
// (run `npm run build` first). Set SPRING_PROFILES_ACTIVE=default to use MySQL instead,
// or BASE_URL to point at an app that is already running.
const profile = process.env.SPRING_PROFILES_ACTIVE || "demo";
const serverCommand = process.env.APP_SERVER_COMMAND
  || `java -jar target/healthops.jar --server.port=${port} --spring.profiles.active=${profile}`;

module.exports = defineConfig({
  testDir: "./tests/e2e",
  timeout: 30000,
  fullyParallel: false,
  workers: 1,
  reporter: [["list"], ["html", { outputFolder: "playwright-report", open: "never" }]],
  expect: {
    timeout: 5000
  },
  use: {
    baseURL,
    trace: "retain-on-failure"
  },
  projects: [
    {
      name: "chromium",
      use: { ...devices["Desktop Chrome"] }
    }
  ],
  webServer: process.env.BASE_URL ? undefined : {
    command: serverCommand,
    url: `${baseURL}/api/health`,
    reuseExistingServer: !process.env.CI,
    timeout: 90000
  }
});
