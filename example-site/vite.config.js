import { defineConfig } from "vite";
import viteCompression from "vite-plugin-compression";
import path from "path";

export default defineConfig({
  plugins: [viteCompression()],
  server: {
    host: "127.0.0.1",
    origin: "http://localhost:5173",
  },
  build: {
    manifest: true,
    outDir: path.resolve(__dirname, "dist"),
    rollupOptions: {
      input: path.resolve(__dirname, "web", "main.js"),
    },
  },
});
