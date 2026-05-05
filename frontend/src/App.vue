
import { provide } from 'vue'
import { useDarkMode } from '@/composables/useDarkMode'

export default {
  name: 'App',
  setup() {
    const { darkMode, toggleDarkMode } = useDarkMode()
    provide('darkMode', darkMode)
    provide('toggleDarkMode', toggleDarkMode)
  }
}
