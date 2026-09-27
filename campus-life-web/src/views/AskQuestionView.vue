<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { questionApi, tagApi } from '@/api'

const router = useRouter()
const tags = ref([])
const form = ref({ title: '', content: '', tagIds: [] })

async function submit() {
  if (!form.value.title.trim()) return ElMessage.warning('标题不能为空')
  if (!form.value.content.trim()) return ElMessage.warning('内容不能为空')
  const id = await questionApi.create(form.value)
  ElMessage.success('发布成功')
  router.push(`/forum/${id}`)
}

onMounted(async () => {
  tags.value = await tagApi.list()
})
</script>

<template>
  <div class="ask-page">
    <el-card shadow="never" class="ask-card">
      <h2>发布问题</h2>
      <el-form label-position="top" @submit.prevent>
        <el-form-item label="标题">
          <el-input v-model="form.title" maxlength="128" show-word-limit placeholder="一句话说清楚你的问题" />
        </el-form-item>
        <el-form-item label="详细描述">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="8"
            placeholder="补充背景、你已尝试过的方法等，回答会更有针对性"
          />
        </el-form-item>
        <el-form-item label="标签（最多 3 个）">
          <el-select
            v-model="form.tagIds"
            multiple
            :multiple-limit="3"
            placeholder="选择标签"
            style="width: 100%"
          >
            <el-option v-for="t in tags" :key="t.id" :label="t.name" :value="t.id" />
          </el-select>
        </el-form-item>
        <div class="bar">
          <el-button @click="router.back()">取消</el-button>
          <el-button type="primary" round @click="submit">发布</el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.ask-page {
  max-width: 760px;
  margin: 0 auto;
  padding: 24px 16px 40px;
}

.ask-card {
  border: none;
  border-radius: 12px;
  box-shadow: 0 1px 5px rgba(13, 150, 104, 0.05);
}

.ask-card h2 {
  margin: 0 0 18px;
  font-size: 19px;
  color: #2c3e50;
}

.bar {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
