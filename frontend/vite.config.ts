import { defineConfig, loadEnv } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig(({ mode }) => {
  const proxy = { '/api': { target: loadEnv(mode, '.', '').API_TARGET || 'http://localhost:8080', changeOrigin: true } }
  return {
    plugins: [react()],
    server: { proxy },
    preview: { proxy },
  }
})
