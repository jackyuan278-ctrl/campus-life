<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { questionApi, answerApi, commentApi, followApi } from '@/api'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const id = Number(route.params.id)

const question = ref(null)
const answers = ref([])
const comments = ref({})
const openIds = ref([])
const commentDraft = ref({})
const answerDraft = ref('')
const isFollowing = ref(false)

const isMine = () => userStore.user && question.value && question.value.userId === userStore.user.userId

function requireLogin() {
  if (!userStore.token) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return false
  }
  return true
}

async function load() {
  question.value = await questionApi.detail(id)
  answers.value = await answerApi.list(id)
  if (userStore.token) {
    const mine = await followApi.mine()
    isFollowing.value = mine.some((u) => u.userId === question.value.userId)
  }
}

function fmtTime(t) {
  return t ? t.replace('T', ' ').slice(0, 16) : ''
}

async function toggleQuestionLike() {
  if (!requireLogin()) return
  try {
    if (question.value.liked) {
      await questionApi.unlike(id)
      question.value.liked = false
      question.value.likeCount -= 1
    } else {
      await questionApi.like(id)
      question.value.liked = true
      question.value.likeCount += 1
    }
  } catch (e) {
    ElMessage.info(e.message || '操作失败')
  }
}

async function toggleAnswerLike(a) {
  if (!requireLogin()) return
  try {
    if (a.liked) {
      await answerApi.unlike(a.id)
      a.liked = false
      a.likeCount -= 1
    } else {
      await answerApi.like(a.id)
      a.liked = true
      a.likeCount += 1
    }
  } catch (e) {
    ElMessage.info(e.message || '操作失败')
  }
}

async function submitAnswer() {
  if (!requireLogin()) return
  if (!answerDraft.value.trim()) return ElMessage.warning('回答内容不能为空')
  await answerApi.create(id, { content: answerDraft.value })
  answerDraft.value = ''
  ElMessage.success('回答成功')
  load()
}

async function toggleComments(a) {
  const idx = openIds.value.indexOf(a.id)
  if (idx >= 0) {
    openIds.value.splice(idx, 1)
  } else {
    openIds.value.push(a.id)
    comments.value[a.id] = await commentApi.list(a.id)
  }
}

async function submitComment(a) {
  if (!requireLogin()) return
  const draft = (commentDraft.value[a.id] || '').trim()
  if (!draft) return ElMessage.warning('评论内容不能为空')
  await commentApi.create(a.id, { content: draft })
  commentDraft.value[a.id] = ''
  comments.value[a.id] = await commentApi.list(a.id)
  a.commentCount += 1
  ElMessage.success('评论成功')
}

async function toggleFollow() {
  if (!requireLogin()) return
  if (isFollowing.value) {
    await followApi.unfollow(question.value.userId)
    isFollowing.value = false
    ElMessage.success('已取消关注')
  } else {
    await followApi.follow({ userId: question.value.userId, nickname: question.value.authorNickname })
    isFollowing.value = true
    ElMessage.success('关注成功')
  }
}

onMounted(load)
</script>

<template>
  <div class="detail-page" v-if="question">
    <el-button text @click="router.back()" class="back-btn">
      <el-icon><ArrowLeft /></el-icon> 返回
    </el-button>

    <el-card shadow="never" class="q-block">
      <h1 class="q-title">{{ question.title }}</h1>
      <div class="q-tags">
        <el-tag v-for="t in question.tags" :key="t" size="small" effect="plain">{{ t }}</el-tag>
      </div>
      <div class="q-author">
        <el-avatar :size="30" class="avatar">{{ (question.authorNickname || '?').slice(0, 1) }}</el-avatar>
        <span class="nickname">{{ question.authorNickname }}</span>
        <span class="time">{{ fmtTime(question.createTime) }}</span>
        <el-button
          v-if="userStore.token && !isMine()"
          size="small"
          round
          :type="isFollowing ? 'info' : 'primary'"
          plain
          @click="toggleFollow"
        >
          {{ isFollowing ? '已关注' : '+ 关注' }}
        </el-button>
      </div>
      <p class="q-content">{{ question.content }}</p>
      <div class="q-actions">
        <span><el-icon><View /></el-icon> {{ question.viewCount }}</span>
        <el-button
          size="small"
          round
          :type="question.liked ? 'primary' : 'default'"
          @click="toggleQuestionLike"
        >
          <el-icon><Star /></el-icon>
          {{ question.liked ? '已赞' : '点赞' }} {{ question.likeCount }}
        </el-button>
      </div>
    </el-card>

    <div class="answer-section">
      <h3 class="section-title">{{ answers.length }} 个回答</h3>

      <el-card shadow="never" class="composer" v-if="userStore.token">
        <el-input
          v-model="answerDraft"
          type="textarea"
          :rows="3"
          maxlength="2000"
          show-word-limit
          placeholder="写下你的回答…"
        />
        <div class="composer-bar">
          <el-button type="primary" round @click="submitAnswer">发布回答</el-button>
        </div>
      </el-card>
      <el-button v-else type="primary" plain round @click="router.push('/login')" class="login-tip">
        登录后参与回答
      </el-button>

      <div v-for="a in answers" :key="a.id" class="answer-item">
        <el-card shadow="never">
          <div class="q-author">
            <el-avatar :size="30" class="avatar">{{ (a.authorNickname || '?').slice(0, 1) }}</el-avatar>
            <span class="nickname">{{ a.authorNickname }}</span>
            <span class="time">{{ fmtTime(a.createTime) }}</span>
          </div>
          <p class="a-content">{{ a.content }}</p>
          <div class="a-actions">
            <el-button
              size="small"
              round
              text
              :type="a.liked ? 'primary' : 'default'"
              @click="toggleAnswerLike(a)"
            >
              <el-icon><Star /></el-icon> {{ a.likeCount }}
            </el-button>
            <el-button size="small" round text @click="toggleComments(a)">
              <el-icon><ChatDotRound /></el-icon> 评论 {{ a.commentCount }}
            </el-button>
          </div>

          <div v-if="openIds.includes(a.id)" class="comment-panel">
            <div v-for="c in comments[a.id]" :key="c.id" class="comment-item">
              <b class="c-nick">{{ c.authorNickname }}：</b>
              <span>{{ c.content }}</span>
              <span class="c-time">{{ fmtTime(c.createTime) }}</span>
            </div>
            <el-empty v-if="!comments[a.id]?.length" description="还没有评论" :image-size="50" />
            <div class="comment-input" v-if="userStore.token">
              <el-input
                v-model="commentDraft[a.id]"
                placeholder="写下你的评论…"
                maxlength="500"
                @keyup.enter="submitComment(a)"
              />
              <el-button size="small" type="primary" round @click="submitComment(a)">评论</el-button>
            </div>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<style scoped>
.detail-page {
  max-width: 860px;
  margin: 0 auto;
  padding: 20px 16px 40px;
}

.back-btn {
  margin-bottom: 12px;
  color: #5b7a8f;
}

.q-block {
  border: none;
  border-radius: 12px;
  box-shadow: 0 1px 5px rgba(13, 150, 104, 0.05);
}

.q-title {
  margin: 4px 0 10px;
  font-size: 21px;
  color: #2c3e50;
}

.q-tags {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.q-author {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.avatar {
  background: #10b981;
  color: #fff;
  font-size: 13px;
}

.nickname {
  font-size: 13px;
  font-weight: 600;
  color: #10b981;
}

.time {
  font-size: 12px;
  color: #a0b0bc;
}

.q-content {
  font-size: 14px;
  line-height: 1.8;
  color: #3d5466;
  white-space: pre-wrap;
  margin: 0 0 14px;
}

.q-actions {
  display: flex;
  align-items: center;
  gap: 18px;
  font-size: 13px;
  color: #8a9aa8;
  border-top: 1px dashed #eef3f6;
  padding-top: 12px;
}

.answer-section {
  margin-top: 18px;
}

.section-title {
  font-size: 16px;
  color: #2c3e50;
  margin: 0 0 12px;
}

.composer {
  border: none;
  border-radius: 12px;
  margin-bottom: 14px;
}

.composer-bar {
  display: flex;
  justify-content: flex-end;
  margin-top: 10px;
}

.login-tip {
  display: block;
  margin: 0 auto 14px;
}

.answer-item {
  margin-bottom: 12px;
}

.answer-item :deep(.el-card) {
  border: none;
  border-radius: 12px;
  box-shadow: 0 1px 5px rgba(13, 150, 104, 0.05);
}

.a-content {
  font-size: 14px;
  line-height: 1.8;
  color: #3d5466;
  white-space: pre-wrap;
  margin: 0 0 8px;
}

.a-actions {
  display: flex;
  gap: 10px;
}

.comment-panel {
  margin-top: 12px;
  padding: 12px;
  background: #f6fafb;
  border-radius: 8px;
}

.comment-item {
  font-size: 13px;
  color: #3d5466;
  line-height: 1.7;
}

.c-nick {
  color: #10b981;
}

.c-time {
  margin-left: 8px;
  font-size: 11px;
  color: #a0b0bc;
}

.comment-input {
  display: flex;
  gap: 8px;
  margin-top: 10px;
}
</style>
