import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src'),
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
