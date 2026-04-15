<template>
  <a-layout class="basic-layout">
    <!-- 顶部导航栏 -->
    <GlobalHeader />

    <!-- 主要内容区域 -->
    <a-layout-content class="main-content">
      <router-view />
    </a-layout-content>

    <!-- 底部版权信息 -->
    <GlobalFooter />
  </a-layout>
</template>

<script setup lang="ts">
import GlobalHeader from '@/components/GlobalHeader.vue'
import GlobalFooter from '@/components/GlobalFooter.vue'
</script>

<style scoped>
.basic-layout {
  min-height: 100vh;
  background: #f5f5f5;
}

.main-content {
  max-width: 1200px;
  width: calc(100% - 32px);
  padding: 24px;
  background: #fff;
  margin: 16px auto 56px;
  border-radius: 10px;
  box-shadow: 0 2px 12px rgb(0 0 0 / 5%);
  min-height: calc(100vh - 220px);
  box-sizing: border-box;
}

@media (max-width: 768px) {
  .main-content {
    width: calc(100% - 16px);
    padding: 16px;
    margin: 12px auto 64px;
    border-radius: 8px;
  }
}
</style>
