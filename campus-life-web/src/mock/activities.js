// 演示活动数据（后端联调后此文件弃用，改走 GET /activities）
export const activities = [
  {
    id: 1,
    title: '2026 迎新晚会',
    description: '校团委主办的大型迎新晚会，包含歌舞、相声、抽奖环节，欢迎全体新生参加。现场凭报名成功的电子凭证入场，请提前 30 分钟到礼堂门口签到。',
    coverUrl: 'https://picsum.photos/seed/welcome2026/800/450',
    location: '大学生活动中心礼堂',
    startTime: '2026-10-08 19:00:00',
    endTime: '2026-10-08 21:30:00',
    signupStartTime: '2026-09-20 08:00:00',
    signupEndTime: '2026-09-30 23:59:59',
    quota: 500,
    status: 1,
    publisherId: 1
  },
  {
    id: 2,
    title: '“新生杯”篮球赛报名',
    description: '面向全校新生的 5v5 篮球赛，队伍自行组队后由队长报名，赛程另行通知。每个学院最多报两支队伍，报名成功后请关注群通知。',
    coverUrl: 'https://picsum.photos/seed/basketball/800/450',
    location: '南区篮球场',
    startTime: '2026-10-12 14:00:00',
    endTime: '2026-10-18 18:00:00',
    signupStartTime: '2026-09-22 08:00:00',
    signupEndTime: '2026-10-08 23:59:59',
    quota: 32,
    status: 1,
    publisherId: 1
  },
  {
    id: 3,
    title: 'AI 编程马拉松（48h）',
    description: '与计算机协会合办的黑客马拉松，题目现场发布，组队或单人参赛均可，提供茶歇与奖品。需自带电脑，鼓励跨专业组队。',
    coverUrl: 'https://picsum.photos/seed/hackathon/800/450',
    location: '信息工程学院实验楼',
    startTime: '2026-10-15 09:00:00',
    endTime: '2026-10-17 09:00:00',
    signupStartTime: '2026-09-25 08:00:00',
    signupEndTime: '2026-10-10 23:59:59',
    quota: 60,
    status: 1,
    publisherId: 1
  },
  {
    id: 4,
    title: '“人工智能与就业”专题讲座',
    description: '邀请企业技术负责人分享 AI 行业就业形势与技能路线。座位极少，拼手速，报满后支持候补，有人取消自动补位。',
    coverUrl: 'https://picsum.photos/seed/lecture/800/450',
    location: '理科楼 B302 报告厅',
    startTime: '2026-09-28 19:00:00',
    endTime: '2026-09-28 21:00:00',
    signupStartTime: '2026-09-26 12:00:00',
    signupEndTime: '2026-09-27 23:59:59',
    quota: 2,
    status: 1,
    publisherId: 1
  },
  {
    id: 5,
    title: '校园定向越野挑战赛',
    description: '三人一组，按地图打卡校园标志性建筑，用时最短的队伍获胜。适合认识新朋友，完赛即有纪念品。',
    coverUrl: 'https://picsum.photos/seed/orienteering/800/450',
    location: '正门广场集合',
    startTime: '2026-10-20 08:30:00',
    endTime: '2026-10-20 12:00:00',
    signupStartTime: '2026-09-26 09:00:00',
    signupEndTime: '2026-10-15 23:59:59',
    quota: 150,
    status: 1,
    publisherId: 1
  },
  {
    id: 6,
    title: '图书馆志愿者招募（秋学期）',
    description: '协助图书上架、阅览室秩序维护，每周至少服务 2 小时，可认证志愿服务时长，期末发放证明。',
    coverUrl: 'https://picsum.photos/seed/library/800/450',
    location: '校图书馆一层服务台',
    startTime: '2026-10-09 08:00:00',
    endTime: '2027-01-10 22:00:00',
    signupStartTime: '2026-09-25 08:00:00',
    signupEndTime: '2026-10-06 23:59:59',
    quota: 40,
    status: 1,
    publisherId: 1
  },
  {
    id: 7,
    title: '十佳歌手大赛（半决赛）',
    description: '校园十佳歌手半决赛，观众入场券限量发放，先到先得。',
    coverUrl: 'https://picsum.photos/seed/singer/800/450',
    location: '音乐厅',
    startTime: '2026-09-24 19:00:00',
    endTime: '2026-09-24 22:00:00',
    signupStartTime: '2026-09-18 08:00:00',
    signupEndTime: '2026-09-23 23:59:59',
    quota: 300,
    status: 2,
    publisherId: 1
  },
  {
    id: 8,
    title: '秋季社团游园会',
    description: '全校社团联合招新游园会（已结束，用于演示历史活动）。',
    coverUrl: 'https://picsum.photos/seed/garden/800/450',
    location: '五四大道',
    startTime: '2026-09-15 09:00:00',
    endTime: '2026-09-15 17:00:00',
    signupStartTime: '2026-09-08 08:00:00',
    signupEndTime: '2026-09-14 23:59:59',
    quota: 200,
    status: 3,
    publisherId: 1
  }
]

export const STATUS_TEXT = {
  1: '报名中',
  2: '进行中',
  3: '已结束',
  4: '已取消'
}
