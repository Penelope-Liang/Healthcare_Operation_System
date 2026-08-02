const { defineConfig, devices } = require("@playwright/test");

const baseURL = process.env.BASE_URL || "http://localhost:8000";

module.exports = defineConfig({
  testDir: "./tests/e2e",
  timeout: 30000,
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
  webServer: {
    command: "/Applications/XAMPP/xamppfiles/bin/php -S localhost:8000 -t public",
    url: baseURL,
    reuseExistingServer: true,
    timeout: 15000
  }
});
