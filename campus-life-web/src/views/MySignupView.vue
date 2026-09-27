<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { signupApi } from '@/api'

const router = useRouter()
const loading = ref(false)
const all = ref([])
const activeTab = ref('signed')

const signedList = computed(() => all.value.filter((s) => s.status === 1))
const waitList = computed(() => all.value.filter((s) => s.status === 2))

async function load() {
  loading.value = true
  try {
    all.value = await signupApi.mine()
  } finally {
    loading.value = false
  }
}

async function handleCancel(item) {
  const isWait = item.status === 2
  try {
    await ElMessageBox.confirm(
      isWait ? `确定取消「${item.activityTitle}」的候补吗？` : `取消「${item.activityTitle}」的报名后名额会释放给候补的同学，确定吗？`,
      '提示',
      { type: 'warning' }
    )
  } catch (e) {
    return
  }
  await signupApi.cancel(item.activityId)
  ElMessage.success('已取消')
  await load()
}

onMounted(load)
</script>

<template>
  <div class="page-container">
    <div class="page-title">我的报名</div>

    <div class="card">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="已报名" name="signed" />
        <el-tab-pane :label="`候补中 (${waitList.length})`" name="waiting" />
      </el-tabs>

      <div v-loading="loading">
        <template v-if="activeTab === 'signed'">
          <el-empty v-if="!signedList.length && !loading" description="还没有报名任何活动">
            <el-button type="primary" @click="router.push('/activities')">去逛逛</el-button>
          </el-empty>
          <div v-for="item in signedList" :key="item.id" class="signup-row">
            <img class="thumb" :src="item.coverUrl" alt="" @click="router.push(`/activities/${item.activityId}`)" />
            <div class="row-main" @click="router.push(`/activities/${item.activityId}`)">
              <div class="row-title">{{ item.activityTitle }}</div>
              <div class="text-muted">{{ item.location }} · {{ item.startTime }}</div>
            </div>
            <el-tag type="success">已报名</el-tag>
            <el-button type="danger" text @click="handleCancel(item)">取消报名</el-button>
          </div>
        </template>

        <template v-else>
          <el-empty v-if="!waitList.length && !loading" description="没有候补中的活动" />
          <div v-for="item in waitList" :key="item.id" class="signup-row">
            <img class="thumb" :src="item.coverUrl" alt="" @click="router.push(`/activities/${item.activityId}`)" />
            <div class="row-main" @click="router.push(`/activities/${item.activityId}`)">
              <div class="row-title">{{ item.activityTitle }}</div>
              <div class="text-muted">{{ item.location }} · {{ item.startTime }}</div>
            </div>
            <el-tag type="warning">候补第 {{ item.waitPosition }} 位</el-tag>
            <el-button type="danger" text @click="handleCancel(item)">取消候补</el-button>
          </div>
          <p v-if="waitList.length" class="text-muted" style="margin-top: 12px">
            候补说明：有人取消报名时，按候补先后顺序自动补位
          </p>
        </template>
      </div>
    </div>
  </div>
</template>

<style scoped>
.signup-row {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 12px 4px;
  border-bottom: 1px solid #f0f3f7;
}

.signup-row:last-child {
  border-bottom: none;
}

.thumb {
  width: 90px;
  height: 56px;
  object-fit: cover;
  border-radius: 8px;
  cursor: pointer;
  flex-shrink: 0;
}

.row-main {
  flex: 1;
  cursor: pointer;
}

.row-title {
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 4px;
}
</style>
