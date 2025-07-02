import { defineConfig, loadEnv } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '');

  return {
    plugins: [react()],
    define: {
      'import.meta.env.VITE_API_URL': JSON.stringify(env.VITE_API_URL),
    },
    server: {
      proxy: {
        '/api': {
          target: env.VITE_API_URL || 'http://localhost:8099',
          changeOrigin: true,
        },
      },
    },
  };
});
