const { defineConfig, devices } = require("@playwright/test");
const fs = require("fs");

const baseURL = process.env.BASE_URL || "http://localhost:8000";
const xamppPhp = "/Applications/XAMPP/xamppfiles/bin/php";
const phpServerCommand = process.env.PHP_SERVER_COMMAND
  || `${fs.existsSync(xamppPhp) ? xamppPhp : "php"} -S localhost:8000 -t public`;

module.exports = defineConfig({
  testDir: "./tests/e2e",
  testIgnore: process.env.DB_E2E ? [] : ["**/database-write.spec.js"],
  timeout: 30000,
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
  webServer: {
    command: phpServerCommand,
    url: baseURL,
    reuseExistingServer: true,
    timeout: 15000
  }
});
