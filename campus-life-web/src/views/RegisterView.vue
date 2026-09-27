<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const form = reactive({ username: '', nickname: '', password: '', confirm: '' })

async function handleRegister() {
  if (!form.username || !form.nickname || !form.password) {
    ElMessage.warning('请填写完整信息')
    return
  }
  if (form.username.length < 3) {
    ElMessage.warning('用户名至少 3 位')
    return
  }
  if (form.password.length < 6) {
    ElMessage.warning('密码至少 6 位')
    return
  }
  if (form.password !== form.confirm) {
    ElMessage.warning('两次输入的密码不一致')
    return
  }
  loading.value = true
  try {
    await userStore.register({ username: form.username, nickname: form.nickname, password: form.password })
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (e) {
    // 错误提示由 request 拦截器统一弹出
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="auth-wrap">
    <div class="auth-card card">
      <h2 class="auth-title">注册</h2>
      <p class="text-muted mb-16">加入校园活动大家庭</p>
      <el-form :model="form" label-position="top" @keyup.enter="handleRegister">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="3-20 位，登录用" size="large" />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="form.nickname" placeholder="展示给其他同学的名字" size="large" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" show-password placeholder="至少 6 位" size="large" />
        </el-form-item>
        <el-form-item label="确认密码">
          <el-input v-model="form.confirm" type="password" show-password placeholder="再输入一次" size="large" />
        </el-form-item>
        <el-button type="primary" size="large" class="submit-btn" :loading="loading" @click="handleRegister">
          注 册
        </el-button>
      </el-form>
      <div class="auth-foot text-muted">
        已有账号？
        <el-link type="primary" @click="router.push('/login')">去登录</el-link>
      </div>
    </div>
  </div>
</template>

<style scoped>
.auth-wrap {
  min-height: calc(100vh - 60px);
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(160deg, #e8f7f1 0%, #eef6fd 100%);
  padding: 24px 16px;
}

.auth-card {
  width: 400px;
  padding: 32px;
}

.auth-title {
  margin: 0 0 4px;
  font-size: 22px;
}

.submit-btn {
  width: 100%;
  margin-top: 8px;
}

.auth-foot {
  margin-top: 16px;
  text-align: center;
}
</style>
