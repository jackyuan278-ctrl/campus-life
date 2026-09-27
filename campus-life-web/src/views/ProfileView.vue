<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { userApi, checkinApi, followApi } from '@/api'

const me = ref(null)
const checkin = ref({ signedToday: false, streak: 0, checkinDates: [] })
const follows = ref([])

async function load() {
  me.value = await userApi.me()
  checkin.value = await checkinApi.mine()
  follows.value = await followApi.mine()
}

async function doCheckin() {
  try {
    const res = await checkinApi.checkin()
    ElMessage.success(`签到成功，已连续 ${res.streak} 天`)
    checkin.value = res
  } catch (e) {
    ElMessage.info(e.message || '今日已签到')
  }
}

async function unfollow(u) {
  try {
    await ElMessageBox.confirm(`确定不再关注 ${u.nickname} 吗？`, '提示', { type: 'warning' })
  } catch {
    return
  }
  await followApi.unfollow(u.userId)
  follows.value = follows.value.filter((x) => x.userId !== u.userId)
  ElMessage.success('已取消关注')
}

onMounted(load)
</script>

<template>
  <div class="profile-page">
    <el-card shadow="never" class="card user-card">
      <div class="user-row">
        <el-avatar :size="56" class="avatar">{{ (me?.nickname || '?').slice(0, 1) }}</el-avatar>
        <div>
          <div class="nickname">{{ me?.nickname || '同学' }}</div>
          <div class="username">@{{ me?.username }}</div>
        </div>
      </div>
    </el-card>

    <el-card shadow="never" class="card">
      <template #header>
        <div class="card-header">
          <b>我的签到</b>
          <el-button type="primary" size="small" round :disabled="checkin.signedToday" @click="doCheckin">
            {{ checkin.signedToday ? '今日已签到' : '今日签到' }}
          </el-button>
        </div>
      </template>
      <div class="streak-row">
        <span class="streak-num">{{ checkin.streak }}</span>
        <span class="streak-label">连续签到天数</span>
      </div>
      <div class="dates-row">
        <span class="dates-label">本月已签：</span>
        <el-tag v-for="d in checkin.checkinDates" :key="d" size="small" effect="plain" class="date-tag">
          {{ d.slice(5) }}
        </el-tag>
        <span v-if="!checkin.checkinDates.length" class="no-date">本月还没有签到记录</span>
      </div>
    </el-card>

    <el-card shadow="never" class="card">
      <template #header><b>我的关注（{{ follows.length }}）</b></template>
      <div v-for="u in follows" :key="u.userId" class="follow-row">
        <el-avatar :size="32" class="avatar small">{{ (u.nickname || '?').slice(0, 1) }}</el-avatar>
        <span class="f-nick">{{ u.nickname }}</span>
        <el-button size="small" round plain @click="unfollow(u)">取消关注</el-button>
      </div>
      <el-empty v-if="!follows.length" description="还没有关注任何人" :image-size="70" />
    </el-card>
  </div>
</template>

<style scoped>
.profile-page {
  max-width: 720px;
  margin: 0 auto;
  padding: 24px 16px 40px;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.card {
  border: none;
  border-radius: 12px;
  box-shadow: 0 1px 5px rgba(13, 150, 104, 0.05);
}

.user-row {
  display: flex;
  align-items: center;
  gap: 14px;
}

.avatar {
  background: #10b981;
  color: #fff;
}

.avatar.small {
  font-size: 13px;
}

.nickname {
  font-size: 16px;
  font-weight: 700;
  color: #2c3e50;
}

.username {
  font-size: 12px;
  color: #a0b0bc;
  margin-top: 2px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.streak-row {
  display: flex;
  align-items: baseline;
  gap: 10px;
  margin-bottom: 12px;
}

.streak-num {
  font-size: 32px;
  font-weight: 800;
  color: #10b981;
}

.streak-label {
  font-size: 13px;
  color: #5b7a8f;
}

.dates-row {
  display: flex;
  align-items: center;
  gap: 6px;
  flex-wrap: wrap;
}

.dates-label {
  font-size: 13px;
  color: #5b7a8f;
}

.date-tag {
  font-family: monospace;
}

.no-date {
  font-size: 13px;
  color: #a0b0bc;
}

.follow-row {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 0;
  border-bottom: 1px dashed #eef3f6;
}

.follow-row:last-child {
  border-bottom: none;
}

.f-nick {
  flex: 1;
  font-size: 14px;
  color: #3d5466;
}
</style>
