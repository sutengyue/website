
import { ref, watch, onMounted } from 'vue'

export function useDarkMode() {
  const darkMode = ref(false)

  const toggleDarkMode = () => {
    darkMode.value = !darkMode.value
    localStorage.setItem('darkMode', darkMode.value.toString())
    updateTheme()
  }

  const updateTheme = () => {
    if (darkMode.value) {
      document.documentElement.classList.add('dark')
    } else {
      document.documentElement.classList.remove('dark')
    }
  }

  onMounted(() => {
    const saved = localStorage.getItem('darkMode')
    if (saved !== null) {
      darkMode.value = saved === 'true'
    } else {
      darkMode.value = window.matchMedia('(prefers-color-scheme: dark)').matches
    }
    updateTheme()
  })

  watch(darkMode, updateTheme)

  return { darkMode, toggleDarkMode }
}
