<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isLoggedIn = computed(() => !!userStore.token)
const activeMenu = computed(() => {
  if (route.path.startsWith('/forum')) return '/forum'
  if (route.path.startsWith('/mine')) return '/mine'
  if (route.path.startsWith('/profile')) return '/profile'
  return '/activities'
})

function handleLogout() {
  ElMessageBox.confirm('确定退出登录吗？', '提示', { type: 'warning' })
    .then(() => {
      userStore.logout()
      router.push('/login')
    })
    .catch(() => {})
}
</script>

<template>
  <div class="app-shell">
    <header class="topbar">
      <div class="topbar-inner">
        <div class="brand" @click="router.push('/activities')">
          <el-icon size="22" color="#10b981"><School /></el-icon>
          <span>校园生活平台</span>
        </div>
        <el-menu :default-active="activeMenu" mode="horizontal" :ellipsis="false" router class="nav-menu">
          <el-menu-item index="/activities">活动大厅</el-menu-item>
          <el-menu-item index="/forum">问答社区</el-menu-item>
          <el-menu-item index="/mine">我的报名</el-menu-item>
          <el-menu-item index="/profile">个人中心</el-menu-item>
        </el-menu>
        <div class="user-area">
          <template v-if="isLoggedIn">
            <el-dropdown @command="handleLogout">
              <span class="user-chip">
                <el-icon><User /></el-icon>
                {{ userStore.user?.nickname || '同学' }}
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <el-button text @click="router.push('/login')">登录</el-button>
            <el-button type="primary" round @click="router.push('/register')">注册</el-button>
          </template>
        </div>
      </div>
    </header>
    <main>
      <router-view />
    </main>
  </div>
</template>

<style scoped>
.app-shell {
  min-height: 100vh;
}

.topbar {
  background: #fff;
  box-shadow: 0 1px 6px rgba(13, 150, 104, 0.06);
  position: sticky;
  top: 0;
  z-index: 100;
}

.topbar-inner {
  max-width: 1100px;
  margin: 0 auto;
  padding: 0 16px;
  display: flex;
  align-items: center;
  gap: 24px;
  height: 60px;
}

.brand {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 17px;
  font-weight: 700;
  cursor: pointer;
  white-space: nowrap;
}

.nav-menu {
  flex: 1;
  border-bottom: none;
}

.user-area {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-chip {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  color: #2c3e50;
  font-size: 14px;
}
</style>
