import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'node:path'
import { fileURLToPath } from 'node:url'

const projectDirectory = fileURLToPath(new URL('.', import.meta.url))

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(projectDirectory, 'src'),
    },
  },
  server: {
    port: 18012,
    proxy: {
      '/api': {
        target: 'http://localhost:18011',
        changeOrigin: true,
      },
      '/uploads': {
        target: 'http://localhost:18011',
        changeOrigin: true,
      },
      '/ws': {
        target: 'http://localhost:18011',
        ws: true,
      },
    },
  },
})
