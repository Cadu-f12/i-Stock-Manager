import { defineConfig, loadEnv } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, '.', '')
  const target = env.API_TARGET || 'http://localhost:8080'
  const proxy = { '^/(api|pastilles|reason-movement|movement|manufacturer|supplier)(/|\\?|$)': { target, changeOrigin: true } }
  return {
    plugins: [react()],
    server: { proxy, watch: { usePolling: env.VITE_USE_POLLING === 'true' } },
    preview: { proxy },
  }
})
