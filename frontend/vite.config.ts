import { defineConfig, loadEnv } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig(({ mode }) => {
  const target = loadEnv(mode, '.', '').API_TARGET || 'http://localhost:8080'
  const proxy = { '^/(api|pastilles|movement|manufacturer|supplier)(/|\\?|$)': { target, changeOrigin: true } }
  return {
    plugins: [react()],
    server: { proxy },
    preview: { proxy },
  }
})
