// 问答社区 mock 数据（USE_MOCK=true 时生效）
// 问题/回答/评论种子 + 用户态（点赞/关注/签到）按登录用户隔离存 localStorage

export const tags = [
  { id: 1, name: '学习' },
  { id: 2, name: '就业' },
  { id: 3, name: '生活' },
  { id: 4, name: '社团' },
  { id: 5, name: '技术' },
  { id: 6, name: '求助' }
]

export function tagName(id) {
  const t = tags.find((x) => x.id === Number(id))
  return t ? t.name : '其他'
}

const Q_KEY = 'campus_mock_questions'
const A_KEY = 'campus_mock_answers'
const C_KEY = 'campus_mock_comments'

function seedQuestions() {
  return [
    { id: 101, title: '大一新生选课有什么技巧？抢不到心仪的课怎么办？', content: '刚入学第一次选课，听说好课都是秒没，求学长学姐支招，比如哪些老师的课比较推荐、捡漏时机是什么时候？', userId: 100001, authorNickname: '晨曦学长', tagIds: [1, 6], viewCount: 328, likeCount: 42, createTime: '2026-09-18T10:00:00' },
    { id: 102, title: '计算机专业大二该怎么规划实习？', content: '大二上学期，会 Java 基础和一点 Spring，想知道现在开始准备实习来得及吗？路线是什么？', userId: 100005, authorNickname: '球场常客', tagIds: [2, 5], viewCount: 512, likeCount: 76, createTime: '2026-09-15T14:30:00' },
    { id: 103, title: '图书馆占座问题真的没办法了吗？', content: '早上八点去图书馆就没位置了，好多位置只有书没有人，学校能管管吗？', userId: 100003, authorNickname: '图书馆小助手', tagIds: [3, 6], viewCount: 267, likeCount: 31, createTime: '2026-09-12T09:00:00' },
    { id: 104, title: '考研和就业怎么选？大三了很焦虑', content: '软件工程大三，成绩中等，家里希望考研，自己觉得代码能力还行想就业，大家怎么看？', userId: 100004, authorNickname: '考研上岸的学姐', tagIds: [1, 2], viewCount: 645, likeCount: 88, createTime: '2026-09-10T20:00:00' },
    { id: 105, title: '社团招新面试一般会问什么问题？', content: '想加入计算机协会和学生会，第一次面试有点紧张，有什么要注意的？', userId: 100005, authorNickname: '球场常客', tagIds: [4], viewCount: 189, likeCount: 15, createTime: '2026-09-08T16:00:00' },
    { id: 106, title: '食堂哪个窗口的饭好吃？求避雷', content: '新生求推荐，南区食堂试了几个窗口都不太满意，有没有公认好吃的？', userId: 100001, authorNickname: '晨曦学长', tagIds: [3], viewCount: 421, likeCount: 53, createTime: '2026-09-05T12:00:00' },
    { id: 107, title: '校园网晚上打游戏卡怎么办？', content: '宿舍晚上 8 点到 11 点 ping 值飘到 200+，是运营商问题还是路由器问题？', userId: 100005, authorNickname: '球场常客', tagIds: [5, 6], viewCount: 156, likeCount: 9, createTime: '2026-09-03T22:00:00' },
    { id: 108, title: '四六级 12 月场现在开始准备来得及吗？', content: '四级还没过，英语基础一般，想问下刷题计划怎么安排？', userId: 100004, authorNickname: '考研上岸的学姐', tagIds: [1], viewCount: 298, likeCount: 24, createTime: '2026-09-01T08:00:00' },
    { id: 109, title: '实验室纳新要求高吗？没竞赛经历能进吗？', content: '对人工智能方向感兴趣，但大一没打过比赛，只有课程项目，还有机会吗？', userId: 100002, authorNickname: '计算机协会', tagIds: [2, 4], viewCount: 234, likeCount: 19, createTime: '2026-08-28T15:00:00' },
    { id: 110, title: '校医院报销流程是怎样的？', content: '感冒看了校医院，说可以去校外医院再报销，具体流程和材料是什么？', userId: 100003, authorNickname: '图书馆小助手', tagIds: [3, 6], viewCount: 98, likeCount: 7, createTime: '2026-08-25T10:30:00' }
  ]
}

function seedAnswers() {
  return [
    { id: 201, questionId: 102, userId: 100001, authorNickname: '晨曦学长', content: '来得及。大二开始是最好的时间点：先把 Java 基础打牢（集合/并发/JVM 八股），然后 Spring Boot 做一两个完整项目，大三寒假就能投日常实习。', likeCount: 18, createTime: '2026-09-16T10:00:00' },
    { id: 202, questionId: 102, userId: 100004, authorNickname: '考研上岸的学姐', content: '补充一点：项目不要只跟着教程抄，包装出自己的东西；面试官更看重你讲清楚为什么这么设计。', likeCount: 12, createTime: '2026-09-16T11:00:00' },
    { id: 203, questionId: 104, userId: 100001, authorNickname: '晨曦学长', content: '两条路都不差。建议先想清楚自己更想要什么：想早点经济独立就去就业，能接受再读三年就考研。软工专业就业面其实很宽。', likeCount: 25, createTime: '2026-09-11T09:00:00' },
    { id: 204, questionId: 104, userId: 100002, authorNickname: '计算机协会', content: '焦虑很正常，但别空焦虑。可以这学期末去投一份实习试试水温，市场反馈比想一百遍都准。', likeCount: 15, createTime: '2026-09-11T15:00:00' },
    { id: 205, questionId: 106, userId: 100003, authorNickname: '图书馆小助手', content: '南区三楼西侧的麻辣香锅窗口公认第一档，饭点排队最长的那家就是。避雷：一楼的"特色盖浇饭"窗口。', likeCount: 33, createTime: '2026-09-06T12:30:00' },
    { id: 206, questionId: 106, userId: 100005, authorNickname: '球场常客', content: '北区清真食堂的牛肉面也可以，加蛋加肉二十块封顶。', likeCount: 11, createTime: '2026-09-06T13:00:00' },
    { id: 207, questionId: 101, userId: 100004, authorNickname: '考研上岸的学姐', content: '选课前先查培养方案，把必修先选上；抢课用校园网比流量稳；第二轮选课的捡漏机会最大，别第一轮没抢到就放弃。', likeCount: 20, createTime: '2026-09-19T09:00:00' },
    { id: 208, questionId: 108, userId: 100001, authorNickname: '晨曦学长', content: '完全来得及。单词每天坚持 100 个，真题从最近三年开始刷，听力精听一周两篇，12 月过线没问题。', likeCount: 9, createTime: '2026-09-02T08:30:00' },
    { id: 209, questionId: 109, userId: 100002, authorNickname: '计算机协会', content: '有机会。课程项目认真做、能讲清楚技术选型就够了，实验室更看重学习态度和动手意愿。', likeCount: 14, createTime: '2026-08-29T10:00:00' },
    { id: 210, questionId: 105, userId: 100002, authorNickname: '计算机协会', content: '自我介绍提前练熟，被问"为什么想加入"时别只说"想学习"，说说你能带来什么。', likeCount: 8, createTime: '2026-09-09T14:00:00' },
    { id: 211, questionId: 103, userId: 100001, authorNickname: '晨曦学长', content: '无解，但可以错峰：晚上 9 点后和周末上午相对空。也可以去教学楼的自习教室，人少安静。', likeCount: 10, createTime: '2026-09-13T09:30:00' }
  ]
}

function seedComments() {
  return [
    { id: 301, answerId: 201, userId: 100005, authorNickname: '球场常客', content: '同意，项目包装太重要了', createTime: '2026-09-16T12:00:00' },
    { id: 302, answerId: 201, userId: 100003, authorNickname: '图书馆小助手', content: '八股哪里看比较好？', createTime: '2026-09-16T13:00:00' },
    { id: 303, answerId: 203, userId: 100005, authorNickname: '球场常客', content: '说得对，不纠结了，先投实习', createTime: '2026-09-11T18:00:00' },
    { id: 304, answerId: 205, userId: 100001, authorNickname: '晨曦学长', content: '麻辣香锅 yyds', createTime: '2026-09-06T13:30:00' }
  ]
}

function load(key, seed) {
  const raw = localStorage.getItem(key)
  if (raw) return JSON.parse(raw)
  const list = seed()
  localStorage.setItem(key, JSON.stringify(list))
  return list
}

export const loadQuestions = () => load(Q_KEY, seedQuestions)
export const saveQuestions = (list) => localStorage.setItem(Q_KEY, JSON.stringify(list))
export const loadAnswers = () => load(A_KEY, seedAnswers)
export const saveAnswers = (list) => localStorage.setItem(A_KEY, JSON.stringify(list))
export const loadComments = () => load(C_KEY, seedComments)
export const saveComments = (list) => localStorage.setItem(C_KEY, JSON.stringify(list))

export function hotScore(q) {
  return (q.viewCount || 0) + (q.likeCount || 0) * 3 + (q.answerCount || 0) * 5
}
