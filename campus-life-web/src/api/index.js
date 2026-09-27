import request from './request'
import { activities } from '@/mock/activities'
import {
  tags,
  tagName,
  hotScore,
  loadQuestions,
  saveQuestions,
  loadAnswers,
  saveAnswers,
  loadComments,
  saveComments
} from '@/mock/forum'

// ============ 后端（campus-life 9090）启动后改成 false 即可对接真实接口 ============
export const USE_MOCK = true

// ---------------- mock 报名状态（localStorage，结构：{id, activityId, userId, status, createTime}） ----------------
const MOCK_SIGNUP_KEY = 'campus_mock_signups'
const MOCK_ME_KEY = 'campus_mock_me'

function loadMockSignups() {
  const raw = localStorage.getItem(MOCK_SIGNUP_KEY)
  return raw ? JSON.parse(raw) : []
}

function saveMockSignups(list) {
  localStorage.setItem(MOCK_SIGNUP_KEY, JSON.stringify(list))
}

function mockUserId() {
  const me = JSON.parse(localStorage.getItem(MOCK_ME_KEY) || 'null')
  return me ? me.userId : null
}

function mockMe() {
  return JSON.parse(localStorage.getItem(MOCK_ME_KEY) || 'null')
}

// 按用户名派生 userId：mock 阶段用不同用户名登录=不同用户，方便演示多人抢名额
function uidFor(username) {
  let h = 0
  for (const ch of username) h = (h * 31 + ch.charCodeAt(0)) % 100000
  return 100000 + h
}

function mockRemaining(activityId) {
  const activity = activities.find((a) => a.id === Number(activityId))
  if (!activity) return 0
  const signed = loadMockSignups().filter((s) => s.activityId === Number(activityId) && s.status === 1).length
  return Math.max(activity.quota - signed, 0)
}

function mockMyStatus(activityId) {
  const uid = mockUserId()
  if (!uid) return null
  const mine = loadMockSignups().find((s) => s.activityId === Number(activityId) && s.userId === uid && s.status !== 3)
  return mine ? mine.status : null
}

// 候补在 activityId 队列中的序号（从 1 开始）
function mockWaitPosition(activityId, signupId) {
  const queue = loadMockSignups()
    .filter((s) => s.activityId === Number(activityId) && s.status === 2)
    .sort((a, b) => a.createTime.localeCompare(b.createTime))
  const idx = queue.findIndex((s) => s.id === signupId)
  return idx + 1
}

function toActivityVO(activity) {
  return {
    ...activity,
    remainingQuota: mockRemaining(activity.id),
    mySignupStatus: mockMyStatus(activity.id)
  }
}

// ---------------- 用户 ----------------
export const userApi = {
  login(data) {
    if (USE_MOCK) {
      const me = { userId: uidFor(data.username), username: data.username, nickname: data.username }
      localStorage.setItem(MOCK_ME_KEY, JSON.stringify(me))
      return Promise.resolve({ token: 'mock-token-' + Date.now(), ...me })
    }
    return request.post('/auth/login', data)
  },
  register(data) {
    if (USE_MOCK) {
      return Promise.resolve(true)
    }
    return request.post('/auth/register', data)
  },
  me() {
    if (USE_MOCK) {
      const me = JSON.parse(localStorage.getItem(MOCK_ME_KEY) || 'null')
      return Promise.resolve(me)
    }
    return request.get('/users/me')
  }
}

// ---------------- 活动 ----------------
export const activityApi = {
  page(params = {}) {
    if (USE_MOCK) {
      const { keyword = '', status = null, page = 1, pageSize = 8 } = params
      let list = activities.filter((a) => a.status !== 4)
      if (status) list = list.filter((a) => a.status === Number(status))
      if (keyword) {
        const kw = keyword.trim()
        list = list.filter((a) => a.title.includes(kw) || a.location.includes(kw))
      }
      list = [...list].sort((a, b) => a.status - b.status || a.startTime.localeCompare(b.startTime))
      const total = list.length
      return Promise.resolve({ list: list.slice((page - 1) * pageSize, page * pageSize).map(toActivityVO), total })
    }
    return request.get('/activities', { params })
  },
  detail(id) {
    if (USE_MOCK) {
      const activity = activities.find((a) => a.id === Number(id))
      return activity ? Promise.resolve(toActivityVO(activity)) : Promise.reject(new Error('活动不存在'))
    }
    return request.get(`/activities/${id}`)
  },
  publish(data) {
    if (USE_MOCK) {
      const activity = { id: Date.now(), status: 1, publisherId: mockUserId(), ...data }
      activities.unshift(activity)
      return Promise.resolve(activity.id)
    }
    return request.post('/activities', data)
  },
  cancel(id) {
    if (USE_MOCK) {
      const activity = activities.find((a) => a.id === Number(id))
      if (activity) activity.status = 4
      return Promise.resolve(true)
    }
    return request.put(`/activities/${id}/cancel`)
  }
}

// ---------------- 报名 / 候补 ----------------
export const signupApi = {
  signup(data) {
    if (USE_MOCK) {
      const uid = mockUserId()
      const activity = activities.find((a) => a.id === Number(data.activityId))
      if (!activity) return Promise.reject(new Error('活动不存在'))
      if (mockMyStatus(activity.id) === 1) return Promise.reject(new Error('请勿重复报名'))
      if (mockRemaining(activity.id) <= 0) return Promise.reject(new Error('名额已满，可加入候补'))
      const list = loadMockSignups()
      list.push({ id: Date.now(), activityId: activity.id, userId: uid, status: 1, createTime: new Date().toISOString() })
      saveMockSignups(list)
      return Promise.resolve(true)
    }
    return request.post('/signups', data)
  },
  joinWaitlist(data) {
    if (USE_MOCK) {
      const uid = mockUserId()
      const activity = activities.find((a) => a.id === Number(data.activityId))
      if (!activity) return Promise.reject(new Error('活动不存在'))
      if (mockMyStatus(activity.id)) return Promise.reject(new Error('你已在报名或候补中'))
      const list = loadMockSignups()
      list.push({ id: Date.now(), activityId: activity.id, userId: uid, status: 2, createTime: new Date().toISOString() })
      saveMockSignups(list)
      return Promise.resolve(true)
    }
    return request.post('/signups/waitlist', data)
  },
  cancel(activityId) {
    if (USE_MOCK) {
      const uid = mockUserId()
      const list = loadMockSignups()
      const mine = list.find((s) => s.activityId === Number(activityId) && s.userId === uid && s.status !== 3)
      if (!mine) return Promise.reject(new Error('未找到报名记录'))
      const wasSigned = mine.status === 1
      mine.status = 3
      if (wasSigned) {
        // 取消的是已报名：名额回补，候补队头自动补位
        const queue = list
          .filter((s) => s.activityId === Number(activityId) && s.status === 2)
          .sort((a, b) => a.createTime.localeCompare(b.createTime))
        if (queue.length) {
          queue[0].status = 1
        }
      }
      saveMockSignups(list)
      return Promise.resolve(true)
    }
    return request.delete(`/signups/${activityId}`)
  },
  mine() {
    if (USE_MOCK) {
      const uid = mockUserId()
      const list = loadMockSignups()
        .filter((s) => s.userId === uid && s.status !== 3)
        .sort((a, b) => b.createTime.localeCompare(a.createTime))
      const result = list.map((s) => {
        const activity = activities.find((a) => a.id === s.activityId)
        return {
          id: s.id,
          activityId: s.activityId,
          activityTitle: activity ? activity.title : '未知活动',
          coverUrl: activity ? activity.coverUrl : '',
          location: activity ? activity.location : '',
          startTime: activity ? activity.startTime : '',
          status: s.status,
          waitPosition: s.status === 2 ? mockWaitPosition(s.activityId, s.id) : null,
          createTime: s.createTime
        }
      })
      return Promise.resolve(result)
    }
    return request.get('/signups/mine')
  }
}

// ---------------- 问答社区 ----------------
const MOCK_LIKE_KEY = 'campus_mock_likes'

function loadMockLikes() {
  const raw = localStorage.getItem(MOCK_LIKE_KEY)
  return raw ? JSON.parse(raw) : {}
}

function saveMockLikes(obj) {
  localStorage.setItem(MOCK_LIKE_KEY, JSON.stringify(obj))
}

// liked 状态按用户存 { uid: ["1:101", "2:201"] }，1=问题 2=回答
function isLiked(type, targetId) {
  const uid = mockUserId()
  if (!uid) return null
  return (loadMockLikes()[uid] || []).includes(`${type}:${targetId}`)
}

function setLiked(type, targetId, liked) {
  const uid = mockUserId()
  if (!uid) return
  const all = loadMockLikes()
  const mine = new Set(all[uid] || [])
  const key = `${type}:${targetId}`
  if (liked) mine.add(key)
  else mine.delete(key)
  all[uid] = [...mine]
  saveMockLikes(all)
}

// 组装展示字段：tags 名称、answerCount/commentCount 由 mock 数据实时计算
function withCounts(q) {
  const ans = loadAnswers().filter((a) => a.questionId === q.id)
  const commentCount = ans.reduce(
    (sum, a) => sum + loadComments().filter((c) => c.answerId === a.id).length,
    0
  )
  return { ...q, tags: (q.tagIds || []).map(tagName), answerCount: ans.length, commentCount }
}

export const questionApi = {
  page(params = {}) {
    if (USE_MOCK) {
      const { keyword = '', tagId = null, sort = 'new', page = 1, pageSize = 10 } = params
      let list = loadQuestions().map(withCounts)
      if (tagId) list = list.filter((q) => q.tagIds.includes(Number(tagId)))
      if (keyword) list = list.filter((q) => q.title.includes(keyword.trim()))
      list = [...list].sort((a, b) =>
        sort === 'hot'
          ? hotScore(b) - hotScore(a) || b.createTime.localeCompare(a.createTime)
          : b.createTime.localeCompare(a.createTime)
      )
      const total = list.length
      return Promise.resolve({
        list: list.slice((page - 1) * pageSize, page * pageSize).map((q) => ({ ...q, liked: isLiked(1, q.id) })),
        total
      })
    }
    return request.get('/questions', { params })
  },
  hot(top = 10) {
    if (USE_MOCK) {
      const list = [...loadQuestions()]
        .map(withCounts)
        .sort((a, b) => hotScore(b) - hotScore(a))
        .slice(0, top)
      return Promise.resolve(list)
    }
    return request.get('/questions/hot', { params: { top } })
  },
  detail(id) {
    if (USE_MOCK) {
      const list = loadQuestions()
      const q = list.find((x) => x.id === Number(id))
      if (!q) return Promise.reject(new Error('问题不存在'))
      q.viewCount += 1
      saveQuestions(list)
      return Promise.resolve({ ...withCounts(q), liked: isLiked(1, q.id) })
    }
    return request.get(`/questions/${id}`)
  },
  create(data) {
    if (USE_MOCK) {
      const me = mockMe()
      const q = {
        id: Date.now(),
        title: data.title,
        content: data.content,
        userId: me ? me.userId : null,
        authorNickname: me ? me.nickname : '游客',
        tagIds: (data.tagIds || []).map(Number),
        viewCount: 0,
        likeCount: 0,
        createTime: new Date().toISOString()
      }
      const list = loadQuestions()
      list.unshift(q)
      saveQuestions(list)
      return Promise.resolve(q.id)
    }
    return request.post('/questions', data)
  },
  like(id) {
    if (USE_MOCK) {
      if (isLiked(1, id)) return Promise.reject(new Error('请勿重复点赞'))
      setLiked(1, id, true)
      const list = loadQuestions()
      const q = list.find((x) => x.id === Number(id))
      if (q) q.likeCount += 1
      saveQuestions(list)
      return Promise.resolve(true)
    }
    return request.post(`/questions/${id}/like`)
  },
  unlike(id) {
    if (USE_MOCK) {
      setLiked(1, id, false)
      const list = loadQuestions()
      const q = list.find((x) => x.id === Number(id))
      if (q && q.likeCount > 0) q.likeCount -= 1
      saveQuestions(list)
      return Promise.resolve(true)
    }
    return request.delete(`/questions/${id}/like`)
  }
}

export const answerApi = {
  list(questionId) {
    if (USE_MOCK) {
      const list = loadAnswers()
        .filter((a) => a.questionId === Number(questionId))
        .sort((a, b) => b.likeCount - a.likeCount || a.createTime.localeCompare(b.createTime))
        .map((a) => ({
          ...a,
          commentCount: loadComments().filter((c) => c.answerId === a.id).length,
          liked: isLiked(2, a.id)
        }))
      return Promise.resolve(list)
    }
    return request.get(`/questions/${questionId}/answers`)
  },
  create(questionId, data) {
    if (USE_MOCK) {
      const me = mockMe()
      const a = {
        id: Date.now(),
        questionId: Number(questionId),
        userId: me ? me.userId : null,
        authorNickname: me ? me.nickname : '游客',
        content: data.content,
        likeCount: 0,
        createTime: new Date().toISOString()
      }
      const list = loadAnswers()
      list.unshift(a)
      saveAnswers(list)
      return Promise.resolve(a.id)
    }
    return request.post(`/questions/${questionId}/answers`, data)
  },
  like(id) {
    if (USE_MOCK) {
      if (isLiked(2, id)) return Promise.reject(new Error('请勿重复点赞'))
      setLiked(2, id, true)
      const list = loadAnswers()
      const a = list.find((x) => x.id === Number(id))
      if (a) a.likeCount += 1
      saveAnswers(list)
      return Promise.resolve(true)
    }
    return request.post(`/answers/${id}/like`)
  },
  unlike(id) {
    if (USE_MOCK) {
      setLiked(2, id, false)
      const list = loadAnswers()
      const a = list.find((x) => x.id === Number(id))
      if (a && a.likeCount > 0) a.likeCount -= 1
      saveAnswers(list)
      return Promise.resolve(true)
    }
    return request.delete(`/answers/${id}/like`)
  }
}

export const commentApi = {
  list(answerId) {
    if (USE_MOCK) {
      const list = loadComments()
        .filter((c) => c.answerId === Number(answerId))
        .sort((a, b) => a.createTime.localeCompare(b.createTime))
      return Promise.resolve(list)
    }
    return request.get(`/answers/${answerId}/comments`)
  },
  create(answerId, data) {
    if (USE_MOCK) {
      const me = mockMe()
      const c = {
        id: Date.now(),
        answerId: Number(answerId),
        userId: me ? me.userId : null,
        authorNickname: me ? me.nickname : '游客',
        content: data.content,
        createTime: new Date().toISOString()
      }
      const list = loadComments()
      list.push(c)
      saveComments(list)
      return Promise.resolve(c.id)
    }
    return request.post(`/answers/${answerId}/comments`, data)
  }
}

// ---------------- 关注 ----------------
const MOCK_FOLLOW_KEY = 'campus_mock_follows'

function loadMockFollows() {
  const raw = localStorage.getItem(MOCK_FOLLOW_KEY)
  return raw ? JSON.parse(raw) : {}
}

export const followApi = {
  mine() {
    if (USE_MOCK) {
      const uid = mockUserId()
      if (!uid) return Promise.resolve([])
      return Promise.resolve(loadMockFollows()[uid] || [])
    }
    return request.get('/follows/mine')
  },
  follow(target) {
    if (USE_MOCK) {
      const uid = mockUserId()
      const all = loadMockFollows()
      const mine = all[uid] || []
      if (!mine.some((u) => u.userId === target.userId)) mine.push(target)
      all[uid] = mine
      localStorage.setItem(MOCK_FOLLOW_KEY, JSON.stringify(all))
      return Promise.resolve(true)
    }
    return request.post(`/follows/${target.userId}`)
  },
  unfollow(targetUserId) {
    if (USE_MOCK) {
      const uid = mockUserId()
      const all = loadMockFollows()
      all[uid] = (all[uid] || []).filter((u) => u.userId !== targetUserId)
      localStorage.setItem(MOCK_FOLLOW_KEY, JSON.stringify(all))
      return Promise.resolve(true)
    }
    return request.delete(`/follows/${targetUserId}`)
  }
}

// ---------------- 签到 ----------------
const MOCK_CHECKIN_KEY = 'campus_mock_checkin'

function todayStr() {
  const d = new Date()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${d.getFullYear()}-${m}-${day}`
}

function monthStr() {
  return todayStr().slice(0, 7)
}

function loadMockCheckin() {
  const raw = localStorage.getItem(MOCK_CHECKIN_KEY)
  return raw ? JSON.parse(raw) : {}
}

function computeStreak(dates) {
  if (!dates.includes(todayStr())) return 0
  let streak = 1
  const d = new Date()
  for (;;) {
    d.setDate(d.getDate() - 1)
    const m = String(d.getMonth() + 1).padStart(2, '0')
    const day = String(d.getDate()).padStart(2, '0')
    const s = `${d.getFullYear()}-${m}-${day}`
    if (dates.includes(s)) streak += 1
    else break
  }
  return streak
}

export const checkinApi = {
  checkin() {
    if (USE_MOCK) {
      const uid = mockUserId()
      if (!uid) return Promise.reject(new Error('请先登录'))
      const all = loadMockCheckin()
      const dates = all[uid] || []
      const today = todayStr()
      if (dates.includes(today)) return Promise.reject(new Error('今日已签到'))
      dates.push(today)
      all[uid] = dates
      localStorage.setItem(MOCK_CHECKIN_KEY, JSON.stringify(all))
      return Promise.resolve({
        signedToday: true,
        streak: computeStreak(dates),
        checkinDates: dates.filter((s) => s.startsWith(monthStr())).sort()
      })
    }
    return request.post('/checkins')
  },
  mine() {
    if (USE_MOCK) {
      const uid = mockUserId()
      const dates = uid ? loadMockCheckin()[uid] || [] : []
      return Promise.resolve({
        signedToday: dates.includes(todayStr()),
        streak: computeStreak(dates),
        checkinDates: dates.filter((s) => s.startsWith(monthStr())).sort()
      })
    }
    return request.get('/checkins/mine')
  }
}

export const tagApi = {
  list() {
    if (USE_MOCK) {
      const counts = {}
      loadQuestions().forEach((q) =>
        (q.tagIds || []).forEach((tid) => {
          counts[tid] = (counts[tid] || 0) + 1
        })
      )
      return Promise.resolve(tags.map((t) => ({ ...t, questionCount: counts[t.id] || 0 })))
    }
    return request.get('/tags')
  }
}
