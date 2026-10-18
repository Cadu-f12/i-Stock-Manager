import { defineConfig, loadEnv } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig(({ mode }) => ({
  plugins: [react()],
  server: { proxy: { '/api': { target: loadEnv(mode, '.', '').API_TARGET || 'http://localhost:8080', changeOrigin: true } } },
}))
