import router from '@/router'
import { message } from 'ant-design-vue'
import { useLoginUserStore } from '@/stores/loginUser'
import ACCESS_ENUM from './accessEnum.ts'
import checkAccess from './checkAccess.ts'

// 是否首次获取登录用户，避免重复请求
let hasInitLoginUser = false

router.beforeEach(async (to, from, next) => {
  const loginUserStore = useLoginUserStore()
  if (!hasInitLoginUser) {
    await loginUserStore.fetchLoginUser()
    hasInitLoginUser = true
  }

  const loginUser = loginUserStore.loginUser
  const needAccess = (to.meta?.access as string) || ACCESS_ENUM.NOT_LOGIN
  if (!checkAccess(loginUser, needAccess)) {
    // 未登录：跳转登录页
    if (!loginUser?.userRole || loginUser.userRole === ACCESS_ENUM.NOT_LOGIN || loginUser.userName === '未登录') {
      next(`/user/login?redirect=${encodeURIComponent(to.fullPath)}`)
      return
    }
    // 已登录但非管理员：回到首页
    message.error('没有访问权限')
    next('/')
    return
  }

  next()
})
