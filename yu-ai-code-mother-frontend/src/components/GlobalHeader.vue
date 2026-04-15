<template>
  <header class="global-header">
    <div class="top-strip"></div>
    <a-layout-header class="header">
      <div class="header-inner">
        <a-row :wrap="false" align="middle">
          <!-- 左侧：Logo和标题 -->
          <a-col flex="360px">
            <div class="header-left">
              <img class="logo" src="/logo.png" alt="Logo" />
              <h1 class="site-title">AI零代码生成应用平台</h1>
            </div>
          </a-col>
          <!-- 中间：导航菜单 -->
          <a-col flex="auto">
            <a-menu
              v-model:selectedKeys="selectedKeys"
              mode="horizontal"
              :items="menuItems"
              @click="handleMenuClick"
            />
          </a-col>
          <!-- 右侧：用户操作区域 -->
          <a-col flex="120px">
            <div class="user-login-status">
              <a-button type="primary" size="middle" shape="round">登录</a-button>
            </div>
          </a-col>
        </a-row>
        </div>
    </a-layout-header>
  </header>
</template>

<script setup lang="ts">
import { h, ref } from 'vue'
import { useRouter } from 'vue-router'
import type { MenuProps } from 'ant-design-vue'

const router = useRouter()
// 当前选中菜单
const selectedKeys = ref<string[]>(['/'])
// 监听路由变化，更新当前选中菜单
router.afterEach((to, from, next) => {
  selectedKeys.value = [to.path]
})

// 菜单配置项
const menuItems = ref([
  {
    key: '/',
    label: '首页',
    title: '首页',
  },
  {
    key: '/about',
    label: '关于',
    title: '关于',
  },
  {
    key: 'others',
    label: h('a', { href: 'https://www.codefather.cn', target: '_blank' }, '编程导航'),
    title: '编程导航',
  },
])

// 处理菜单点击
const handleMenuClick: MenuProps['onClick'] = (e) => {
  const key = e.key as string
  selectedKeys.value = [key]
  // 跳转到对应页面
  if (key.startsWith('/')) {
    router.push(key)
  }
}
</script>

<style scoped>
.header {
  background: #ffffff;
  padding: 0;
  height: 64px;
  line-height: 64px;
  box-shadow: 0 2px 8px rgb(0 0 0 / 6%);
}

.top-strip {
  height: 36px;
  background: #1f1f1f;
}

.header-inner {
  max-width: 1200px;
  height: 100%;
  margin: 0 auto;
  padding: 0 16px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
}

.logo {
  height: 32px;
  width: 32px;
  border-radius: 4px;
  object-fit: cover;
}

.site-title {
  margin: 0;
  font-size: 15px;
  font-weight: 600;
  color: rgba(0, 0, 0, 0.88);
  white-space: nowrap;
}

.user-login-status {
  display: flex;
  justify-content: flex-end;
}

:deep(.ant-menu-horizontal) {
  border-bottom: none;
  line-height: 62px;
}

:deep(.ant-menu-horizontal > .ant-menu-item) {
  padding-inline: 14px;
  margin-inline: 4px;
}

@media (max-width: 768px) {
  .top-strip {
    height: 28px;
  }

  .header {
    height: auto;
    line-height: normal;
  }

  .header-inner {
    padding: 8px 12px;
  }

  .site-title {
    font-size: 14px;
  }
}
</style>
