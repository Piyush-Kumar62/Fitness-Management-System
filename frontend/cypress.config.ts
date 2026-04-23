import { defineConfig } from "cypress";

export default defineConfig({
  e2e: {
    baseUrl: "http://localhost:4200", // Default Angular port
    setupNodeEvents(on, config) {
      // implement node event listeners here
    },
    supportFile: false // For simplicity in this project
  },
});
