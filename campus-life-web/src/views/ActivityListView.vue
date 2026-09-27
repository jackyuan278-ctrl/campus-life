<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { activityApi } from '@/api'
import { STATUS_TEXT } from '@/mock/activities'

const router = useRouter()
const loading = ref(false)
const list = ref([])
const total = ref(0)
const query = reactive({ keyword: '', status: null, page: 1, pageSize: 8 })

const statusOptions = [
  { label: '全部状态', value: null },
  { label: '报名中', value: 1 },
  { label: '进行中', value: 2 },
  { label: '已结束', value: 3 }
]

async function load() {
  loading.value = true
  try {
    const data = await activityApi.page({ ...query })
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  query.page = 1
  load()
}

function handleReset() {
  query.keyword = ''
  query.status = null
  query.page = 1
  load()
}

function statusTagType(status) {
  return { 1: 'success', 2: 'primary', 3: 'info', 4: 'danger' }[status] || 'info'
}

onMounted(load)
</script>

<template>
  <div class="page-container">
    <div class="hero-banner">
      <h1>校园活动大厅</h1>
      <p>发现身边的好活动，热门名额先到先得，报满还能候补</p>
    </div>

    <div class="card mb-16">
      <el-form inline>
        <el-form-item label="关键词">
          <el-input
            v-model="query.keyword"
            placeholder="活动名称 / 地点"
            clearable
            style="width: 220px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" style="width: 130px">
            <el-option v-for="opt in statusOptions" :key="String(opt.value)" :label="opt.label" :value="opt.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch"><el-icon><Search /></el-icon>搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div v-loading="loading">
      <el-row v-if="list.length" :gutter="16">
        <el-col v-for="item in list" :key="item.id" :xs="24" :sm="12" :md="6" class="mb-16">
          <div class="card activity-card" @click="router.push('/activities/' + item.id)">
            <img class="cover" :src="item.coverUrl" :alt="item.title" />
            <div class="body">
              <div class="title">{{ item.title }}</div>
              <div class="meta">
                <div><el-icon class="meta-icon"><Location /></el-icon>{{ item.location }}</div>
                <div><el-icon class="meta-icon"><Clock /></el-icon>{{ item.startTime }}</div>
                <div>
                  <el-tag :type="statusTagType(item.status)" size="small">{{ STATUS_TEXT[item.status] }}</el-tag>
                  <el-tag v-if="item.status === 1" type="success" size="small" effect="plain" class="ml-8">
                    剩余 {{ item.remainingQuota }}
                  </el-tag>
                  <el-tag v-if="item.status === 1 && item.remainingQuota === 0" type="warning" size="small" effect="plain" class="ml-8">
                    可候补
                  </el-tag>
                </div>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
      <el-empty v-else-if="!loading" description="没有找到相关活动" />
      <div v-if="total > query.pageSize" class="mt-16" style="text-align: center">
        <el-pagination
          v-model:current-page="query.page"
          :page-size="query.pageSize"
          :total="total"
          layout="prev, pager, next, total"
          @current-change="load"
        />
      </div>
    </div>
  </div>
</template>

<style scoped>
.ml-8 {
  margin-left: 8px;
}
</style>
