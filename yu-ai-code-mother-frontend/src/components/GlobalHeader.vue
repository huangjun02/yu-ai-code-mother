<template>
  <header class="global-header">
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
          <a-col flex="200px">
            <div class="user-login-status">
              <div v-if="isLoggedIn">
                <a-dropdown placement="bottomCenter" trigger="click">
                  <a-space class="user-trigger">
                    <a-avatar :src="loginUserStore.loginUser.userAvatar" />
                    <span>{{ loginUserStore.loginUser.userName ?? '无名' }}</span>
                  </a-space>
                  <template #overlay>
                    <div class="user-dropdown-card">
                      <a-button class="logout-btn" type="text" @click="handleLogout">
                        <LogoutOutlined class="logout-icon" />
                        退出登录
                      </a-button>
                    </div>
                  </template>
                </a-dropdown>
              </div>
              <div v-else>
                <a-button type="primary" href="/user/login">登录</a-button>
              </div>
            </div>
          </a-col>
        </a-row>
      </div>
    </a-layout-header>
  </header>
</template>

<script setup lang="ts">
import { h, ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import type { MenuProps } from 'ant-design-vue'
import { message } from 'ant-design-vue'
import { HomeOutlined, LogoutOutlined } from '@ant-design/icons-vue'
import { userLogout } from '@/api/userController.ts'

// JS 中引入 Store
import { useLoginUserStore } from '@/stores/loginUser.ts'
const loginUserStore = useLoginUserStore()

const router = useRouter()

// 判断是否已登录（兼容 id 为字符串的情况）
const isLoggedIn = computed(() => {
  const user = loginUserStore.loginUser
  return user && user.id && user.userName && user.userName !== '未登录'
})

// 组件挂载时获取登录用户信息
onMounted(async () => {
  await loginUserStore.fetchLoginUser()
  console.log('登录用户信息:', loginUserStore.loginUser)
})

const handleLogout = async () => {
  const res = await userLogout()
  if (res.data.code === 0) {
    loginUserStore.setLoginUser({
      userName: '未登录'
    })
    message.success('退出登录成功')
    await router.push('/user/login')
    return
  }
  message.error(res.data.message || '退出登录失败')
}

// 当前选中菜单
const selectedKeys = ref<string[]>(['/'])
// 监听路由变化，更新当前选中菜单
router.afterEach((to, from, next) => {
  selectedKeys.value = [to.path]
})

// 菜单配置项
const originItems: MenuProps['items'] = [
  {
    key: '/',
    icon: () => h(HomeOutlined),
    label: '主页',
    title: '主页',
  },
  {
    key: '/admin/userManage',
    label: '用户管理',
    title: '用户管理',
  },
  {
    key: 'others',
    label: h('a', { href: 'https://www.codefather.cn', target: '_blank' }, '编程导航'),
    title: '编程导航',
  },
]

// 过滤菜单项
const filterMenus = (menus = [] as MenuProps['items']) => {
  return menus?.filter((menu) => {
    const menuKey = menu?.key as string
    if (menuKey?.startsWith('/admin')) {
      const loginUser = loginUserStore.loginUser
      if (!loginUser || loginUser.userRole !== 'admin') {
        return false
      }
    }
    return true
  })
}

// 展示在菜单的路由数组的
const menuItems = computed<MenuProps['items']>(() => filterMenus(originItems))

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
  align-items: center;
}

.user-trigger {
  cursor: pointer;
}

.user-dropdown-card {
  margin-top: 10px;
  padding: 8px;
  border-radius: 12px;
  background: #fff;
  box-shadow: 0 8px 20px rgb(0 0 0 / 10%);
}

.logout-btn {
  height: 42px;
  border-radius: 10px;
  font-size: 20px;
  color: rgba(0, 0, 0, 0.88);
}

.logout-icon {
  margin-right: 8px;
  font-size: 18px;
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
