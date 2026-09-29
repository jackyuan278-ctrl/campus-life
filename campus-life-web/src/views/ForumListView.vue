<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { questionApi, checkinApi, tagApi } from '@/api'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const sort = ref('new')
const keyword = ref('')
const tagId = ref(null)
const page = ref(1)
const pageSize = 10
const total = ref(0)
const list = ref([])
const hotList = ref([])
const tags = ref([])
const checkin = ref({ signedToday: false, streak: 0, checkinDates: [] })

async function load() {
  const res = await questionApi.page({
    keyword: keyword.value,
    tagId: tagId.value,
    sort: sort.value,
    page: page.value,
    pageSize
  })
  list.value = res.list
  total.value = Number(res.total)
}

async function loadSide() {
  hotList.value = await questionApi.hot(8)
  tags.value = await tagApi.list()
}

async function loadCheckin() {
  checkin.value = await checkinApi.mine()
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

function search() {
  page.value = 1
  load()
}

function goAsk() {
  if (!userStore.token) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  router.push('/forum/ask')
}

function fmtTime(t) {
  return t ? t.slice(0, 10) : ''
}

function score(q) {
  return q.viewCount + q.likeCount * 3 + q.answerCount * 5
}

onMounted(() => {
  load()
  loadSide()
  if (userStore.token) loadCheckin()
})
</script>

<template>
  <div class="forum-page">
    <div class="hero">
      <div>
        <h1>校园问答社区</h1>
        <p>学习、就业、生活——有问题，一起答</p>
      </div>
      <div class="checkin-box" v-if="userStore.token">
        <el-button type="primary" round :disabled="checkin.signedToday" @click="doCheckin">
          {{ checkin.signedToday ? '今日已签到' : '今日签到' }}
        </el-button>
        <span class="streak">已连续 {{ checkin.streak }} 天</span>
      </div>
      <el-button v-else text @click="router.push('/login')">登录后可签到</el-button>
    </div>

    <div class="forum-layout">
      <div class="main-col">
        <div class="toolbar">
          <el-radio-group v-model="sort" @change="search">
            <el-radio-button value="new">最新</el-radio-button>
            <el-radio-button value="hot">最热</el-radio-button>
          </el-radio-group>
          <el-input
            v-model="keyword"
            placeholder="搜索问题标题"
            clearable
            style="width: 220px"
            @keyup.enter="search"
            @clear="search"
          >
            <template #append>
              <el-button @click="search"><el-icon><Search /></el-icon></el-button>
            </template>
          </el-input>
          <el-select v-model="tagId" placeholder="全部标签" clearable style="width: 130px" @change="search">
            <el-option v-for="t in tags" :key="t.id" :label="t.name" :value="t.id" />
          </el-select>
          <el-button type="primary" round @click="goAsk">我要提问</el-button>
        </div>

        <div class="q-list">
          <div v-for="q in list" :key="q.id" class="q-card" @click="router.push(`/forum/${q.id}`)">
            <div class="q-title">
              <span class="q-text">{{ q.title }}</span>
              <el-tag v-for="t in q.tags" :key="t" size="small" effect="plain">{{ t }}</el-tag>
            </div>
            <div class="q-meta">
              <span class="author">{{ q.authorNickname }}</span>
              <span><el-icon><View /></el-icon> {{ q.viewCount }}</span>
              <span><el-icon><Star /></el-icon> {{ q.likeCount }}</span>
              <span><el-icon><ChatDotRound /></el-icon> {{ q.answerCount }}</span>
              <span class="q-time">{{ fmtTime(q.createTime) }}</span>
            </div>
          </div>
          <el-empty v-if="!list.length" description="暂无问题，来提第一个吧" />
        </div>

        <div class="pager" v-if="total > pageSize">
          <el-pagination
            layout="prev, pager, next"
            :total="total"
            :page-size="pageSize"
            v-model:current-page="page"
            @current-change="load"
          />
        </div>
      </div>

      <div class="side-col">
        <el-card shadow="never" class="hot-card">
          <template #header>
            <div class="hot-header">
              <b>热点榜</b>
              <span class="hot-tip">浏览×1 + 点赞×3 + 回答×5</span>
            </div>
          </template>
          <div v-for="(q, i) in hotList" :key="q.id" class="hot-item" @click="router.push(`/forum/${q.id}`)">
            <span class="rank" :class="{ top3: i < 3 }">{{ i + 1 }}</span>
            <span class="hot-title">{{ q.title }}</span>
            <span class="hot-score">{{ score(q) }}</span>
          </div>
          <el-empty v-if="!hotList.length" description="暂无数据" :image-size="60" />
        </el-card>
      </div>
    </div>
  </div>
</template>

<style scoped>
.forum-page {
  max-width: 1100px;
  margin: 0 auto;
  padding: 20px 16px 40px;
}

.hero {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 22px 28px;
  margin-bottom: 18px;
  border-radius: 14px;
  background: linear-gradient(120deg, #e6f7f4, #eaf4fb);
}

.hero h1 {
  margin: 0 0 6px;
  font-size: 22px;
  color: #1f7a63;
}

.hero p {
  margin: 0;
  font-size: 13px;
  color: #5b7a8f;
}

.checkin-box {
  display: flex;
  align-items: center;
  gap: 10px;
}

.streak {
  font-size: 13px;
  color: #10b981;
  font-weight: 600;
  white-space: nowrap;
}

.forum-layout {
  display: flex;
  gap: 18px;
  align-items: flex-start;
}

.main-col {
  flex: 1;
  min-width: 0;
}

.side-col {
  width: 300px;
  flex-shrink: 0;
}

.toolbar {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
  margin-bottom: 14px;
}

.q-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.q-card {
  background: #fff;
  border-radius: 10px;
  padding: 14px 18px;
  cursor: pointer;
  box-shadow: 0 1px 5px rgba(13, 150, 104, 0.05);
  transition: box-shadow 0.2s;
}

.q-card:hover {
  box-shadow: 0 3px 12px rgba(13, 150, 104, 0.14);
}

.q-title {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 10px;
}

.q-text {
  font-size: 15px;
  font-weight: 600;
  color: #2c3e50;
  margin-right: 4px;
}

.q-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 12px;
  color: #8a9aa8;
}

.q-meta .el-icon {
  vertical-align: -2px;
}

.author {
  color: #10b981;
  font-weight: 600;
}

.q-time {
  margin-left: auto;
}

.pager {
  display: flex;
  justify-content: center;
  margin-top: 18px;
}

.hot-card {
  border-radius: 12px;
  border: none;
  box-shadow: 0 1px 5px rgba(13, 150, 104, 0.05);
}

.hot-header {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.hot-tip {
  font-size: 11px;
  color: #a0b0bc;
}

.hot-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 9px 4px;
  cursor: pointer;
  border-bottom: 1px dashed #eef3f6;
}

.hot-item:last-child {
  border-bottom: none;
}

.rank {
  width: 20px;
  height: 20px;
  line-height: 20px;
  text-align: center;
  border-radius: 5px;
  font-size: 12px;
  color: #fff;
  background: #c3d0d9;
  flex-shrink: 0;
}

.rank.top3 {
  background: #10b981;
}

.hot-title {
  flex: 1;
  font-size: 13px;
  color: #3d5466;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.hot-score {
  font-size: 12px;
  color: #f59e0b;
  font-weight: 600;
}
</style>
