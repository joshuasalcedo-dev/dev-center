import path from 'path'
import { defineConfig } from 'vite'
import react, { reactCompilerPreset } from '@vitejs/plugin-react'
import babel from '@rolldown/plugin-babel'

const uiSrc = path.resolve(__dirname, '../../packages/ui/src')

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    react(),
    babel({ presets: [reactCompilerPreset()] })
  ],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
      '@workspace/ui/lib': path.resolve(uiSrc, 'lib'),
      '@workspace/ui/components': path.resolve(uiSrc, 'components'),
      '@workspace/ui/hooks': path.resolve(uiSrc, 'hooks'),
    },
  },
})
