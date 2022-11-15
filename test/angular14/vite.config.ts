/// <reference types="vitest" />

import { defineConfig } from "vite";
import angular from "@analogjs/vite-plugin-angular";

// https://vitejs.dev/config/
export default defineConfig(({ mode }) => ({
  root: "src",
  publicDir: "assets",
  build: {
    minify: false,
    outDir: `../build`,
    emptyOutDir: true,
    target: "es2020",
    assetsDir: "assets",
    manifest: false,
    rollupOptions: {
      output: {
        entryFileNames: `assets/[name].js`,
        chunkFileNames: `assets/[name].js`,
        assetFileNames: `assets/[name].[ext]`,
        banner: `
        window.onerror = function(msg, url, lineNo, columnNo, error) {
          alert("frontend error");
          console.log("frontend error");
          let string = msg.toString().toLowerCase();
          let substring = "script error";
          if (string.indexOf(substring) > -1) {
            alert("Script Error: See Browser Console for Detail");
          } else {
            let message = ["Message: " + msg, "URL: " + url, "Line: " + lineNo, "Column: " + columnNo, "Error object: " + JSON.stringify(error)].join(" - ");
            console.log(message);
            alert(message);
          }
          return false;
        };
        `,
      },
    },
  },
  resolve: {
    mainFields: ["module"],
  },
  plugins: [angular()],
  define: {
    "import.meta.vitest": mode !== "production",
  },
}));
