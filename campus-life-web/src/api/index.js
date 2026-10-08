import request from './request'

// ---------------- 用户 ----------------
export const userApi = {
  login(data) {
    return request.post('/auth/login', data)
  },
  register(data) {
    return request.post('/auth/register', data)
  },
  me() {
    return request.get('/users/me')
  }
}

// ---------------- 活动 ----------------
export const activityApi = {
  page(params = {}) {
    return request.get('/activities', { params })
  },
  search(params = {}) {
    return request.get('/activities/search', { params })
  },
  detail(id) {
    return request.get(`/activities/${id}`)
  },
  publish(data) {
    return request.post('/activities', data)
  },
  cancel(id) {
    return request.put(`/activities/${id}/cancel`)
  }
}

// ---------------- 报名 / 候补 ----------------
export const signupApi = {
  signup(data) {
    return request.post('/signups', data)
  },
  joinWaitlist(data) {
    return request.post('/signups/waitlist', data)
  },
  cancel(activityId) {
    return request.delete(`/signups/${activityId}`)
  },
  mine() {
    return request.get('/signups/mine')
  }
}

// ---------------- 问答社区 ----------------
export const questionApi = {
  page(params = {}) {
    return request.get('/questions', { params })
  },
  hot(top = 10) {
    return request.get('/questions/hot', { params: { top } })
  },
  detail(id) {
    return request.get(`/questions/${id}`)
  },
  create(data) {
    return request.post('/questions', data)
  },
  like(id) {
    return request.post(`/questions/${id}/like`)
  },
  unlike(id) {
    return request.delete(`/questions/${id}/like`)
  }
}

export const answerApi = {
  list(questionId) {
    return request.get(`/questions/${questionId}/answers`)
  },
  create(questionId, data) {
    return request.post(`/questions/${questionId}/answers`, data)
  },
  like(id) {
    return request.post(`/answers/${id}/like`)
  },
  unlike(id) {
    return request.delete(`/answers/${id}/like`)
  }
}

export const commentApi = {
  list(answerId) {
    return request.get(`/answers/${answerId}/comments`)
  },
  create(answerId, data) {
    return request.post(`/answers/${answerId}/comments`, data)
  }
}

// ---------------- 关注（后端返回 UserVO，用户主键字段是 id） ----------------
export const followApi = {
  mine() {
    return request.get('/follows/mine')
  },
  follow(targetUserId) {
    return request.post(`/follows/${targetUserId}`)
  },
  unfollow(targetUserId) {
    return request.delete(`/follows/${targetUserId}`)
  }
}

// ---------------- 签到 ----------------
export const checkinApi = {
  checkin() {
    return request.post('/checkins')
  },
  mine() {
    return request.get('/checkins/mine')
  }
}

export const tagApi = {
  list() {
    return request.get('/tags')
  }
}
