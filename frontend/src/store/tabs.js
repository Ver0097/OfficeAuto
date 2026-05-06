import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useTabsStore = defineStore('tabs', () => {
  // 已打开的标签列表
  const tabs = ref([
    { path: '/', title: '首页', closable: false }
  ])

  // 当前激活的标签路径
  const activePath = ref('/')

  // 添加标签
  function addTab(path, title) {
    // 检查是否已存在
    const exists = tabs.value.find(tab => tab.path === path)
    if (!exists) {
      tabs.value.push({ path, title, closable: path !== '/' })
    }
    activePath.value = path
  }

  // 关闭标签
  function closeTab(path, router) {
    const index = tabs.value.findIndex(tab => tab.path === path)
    if (index > -1 && tabs.value[index].closable) {
      tabs.value.splice(index, 1)
      // 如果关闭的是当前激活的标签，切换到上一个或首页
      if (activePath.value === path) {
        const newIndex = Math.min(index, tabs.value.length - 1)
        activePath.value = tabs.value[newIndex].path
        router.push(activePath.value)
      }
    }
  }

  // 切换标签
  function switchTab(path, router) {
    activePath.value = path
    router.push(path)
  }

  // 获取当前标签标题
  const currentTitle = computed(() => {
    const tab = tabs.value.find(t => t.path === activePath.value)
    return tab?.title || ''
  })

  return {
    tabs,
    activePath,
    addTab,
    closeTab,
    switchTab,
    currentTitle
  }
})