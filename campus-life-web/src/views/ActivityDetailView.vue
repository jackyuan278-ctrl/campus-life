<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { activityApi, signupApi } from '@/api'
import { STATUS_TEXT } from '@/mock/activities'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const activity = ref(null)
const loading = ref(false)
const acting = ref(false)

const isLoggedIn = computed(() => !!userStore.token)

async function load() {
  loading.value = true
  try {
    activity.value = await activityApi.detail(route.params.id)
  } finally {
    loading.value = false
  }
}

async function handleSignup() {
  acting.value = true
  try {
    await signupApi.signup({ activityId: activity.value.id })
    ElMessage.success('报名成功')
    await load()
  } catch (e) {
    if (e.message && e.message.includes('名额已满')) {
      ElMessage.warning('手慢了，名额已满，可以加入候补')
      await load()
    }
  } finally {
    acting.value = false
  }
}

async function handleJoinWaitlist() {
  acting.value = true
  try {
    await signupApi.joinWaitlist({ activityId: activity.value.id })
    ElMessage.success('已加入候补，有人取消会自动补位')
    await load()
  } finally {
    acting.value = false
  }
}

async function handleCancel() {
  const isWait = activity.value.mySignupStatus === 2
  try {
    await ElMessageBox.confirm(
      isWait ? '确定取消候补吗？' : '取消报名后名额会释放给候补的同学，确定吗？',
      '提示',
      { type: 'warning' }
    )
  } catch (e) {
    return
  }
  acting.value = true
  try {
    await signupApi.cancel(activity.value.id)
    ElMessage.success('已取消')
    await load()
  } finally {
    acting.value = false
  }
}

onMounted(load)
</script>

<template>
  <div class="page-container">
    <el-button text @click="router.back()"><el-icon><ArrowLeft /></el-icon>返回</el-button>

    <div v-loading="loading">
      <template v-if="activity">
        <div class="card detail-card">
          <img class="detail-cover" :src="activity.coverUrl" :alt="activity.title" />
          <div class="detail-main">
            <div class="detail-title">
              {{ activity.title }}
              <el-tag :type="{ 1: 'success', 2: 'primary', 3: 'info', 4: 'danger' }[activity.status]" class="ml-8">
                {{ STATUS_TEXT[activity.status] }}
              </el-tag>
            </div>
            <div class="info-grid">
              <div class="info-item">
                <el-icon color="#0ea5e9"><Location /></el-icon>
                <span>{{ activity.location }}</span>
              </div>
              <div class="info-item">
                <el-icon color="#0ea5e9"><Clock /></el-icon>
                <span>{{ activity.startTime }} ~ {{ activity.endTime }}</span>
              </div>
              <div class="info-item">
                <el-icon color="#0ea5e9"><Flag /></el-icon>
                <span>报名时间 {{ activity.signupStartTime }} ~ {{ activity.signupEndTime }}</span>
              </div>
              <div class="info-item">
                <el-icon color="#0ea5e9"><User /></el-icon>
                <span>
                  名额 {{ activity.quota }} 人
                  <template v-if="activity.status === 1">，剩余 <b style="color: #10b981">{{ activity.remainingQuota }}</b></template>
                </span>
              </div>
            </div>
          </div>
        </div>

        <div class="card mt-16">
          <div class="section-title">活动介绍</div>
          <p class="desc">{{ activity.description }}</p>
        </div>

        <div class="card mt-16 action-bar">
          <template v-if="!isLoggedIn">
            <el-button type="primary" size="large" @click="router.push('/login')">登录后即可报名</el-button>
          </template>

          <template v-else-if="activity.status === 1">
            <template v-if="activity.mySignupStatus === 1">
              <el-tag type="success" size="large">已报名</el-tag>
              <el-button type="danger" plain :loading="acting" @click="handleCancel">取消报名</el-button>
            </template>
            <template v-else-if="activity.mySignupStatus === 2">
              <el-tag type="warning" size="large">候补中</el-tag>
              <el-button type="danger" plain :loading="acting" @click="handleCancel">取消候补</el-button>
            </template>
            <template v-else-if="activity.remainingQuota > 0">
              <el-button type="primary" size="large" :loading="acting" @click="handleSignup">立即报名</el-button>
            </template>
            <template v-else>
              <el-tag type="danger" size="large">名额已满</el-tag>
              <el-button type="warning" size="large" :loading="acting" @click="handleJoinWaitlist">加入候补</el-button>
            </template>
          </template>

          <template v-else-if="activity.status === 2">
            <el-tag type="primary" size="large">活动进行中，报名已截止</el-tag>
          </template>
          <template v-else-if="activity.status === 3">
            <el-tag type="info" size="large">活动已结束</el-tag>
          </template>
          <template v-else>
            <el-tag type="danger" size="large">活动已取消</el-tag>
          </template>
        </div>
      </template>
    </div>
  </div>
</template>

<style scoped>
.detail-card {
  display: flex;
  gap: 20px;
  padding: 0;
  overflow: hidden;
}

.detail-cover {
  width: 380px;
  height: 240px;
  object-fit: cover;
  flex-shrink: 0;
}

.detail-main {
  padding: 20px 20px 20px 0;
  flex: 1;
}

.detail-title {
  font-size: 20px;
  font-weight: 700;
  margin-bottom: 16px;
}

.ml-8 {
  margin-left: 8px;
}

.info-grid {
  display: grid;
  gap: 12px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #41556b;
}

.section-title {
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 10px;
}

.desc {
  margin: 0;
  line-height: 1.8;
  color: #41556b;
  white-space: pre-wrap;
}

.action-bar {
  display: flex;
  align-items: center;
  gap: 16px;
}

@media (max-width: 768px) {
  .detail-card {
    flex-direction: column;
  }

  .detail-cover {
    width: 100%;
    height: 180px;
  }

  .detail-main {
    padding: 16px;
  }
}
</style>
