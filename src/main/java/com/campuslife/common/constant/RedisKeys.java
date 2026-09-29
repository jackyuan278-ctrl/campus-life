package com.campuslife.common.constant;

/**
 * Redis key 统一定义。
 * 口诀：cache: 只做 GET/SET/DEL，lock: 只做 setIfAbsent/DEL，账本 key 只做 Z* 命令。
 * 需要拼 id 的 key 一律走这里的静态方法，不要在业务里手搓冒号。
 */
public final class RedisKeys {

    private RedisKeys() {
    }

    /** 热度账本（ZSet：member=问题ID，score=浏览*1 + 点赞*3 + 回复*5） */
    public static final String HOT_QUESTIONS = "hot:questions";

    /** 热榜快照（String，TTL 由 queryHot 随机 5-11 分钟） */
    public static final String HOT_QUESTIONS_CACHE = "cache:hot:questions";

    /** 热榜重建互斥锁（30 秒兜底过期） */
    public static final String HOT_QUESTIONS_LOCK = "lock:cache:hot:questions";

    /** 问题详情快照（TTL 30 分钟，改 DB 后必须删） */
    public static String questionCache(Long questionId) {
        return "cache:question:" + questionId;
    }

    /** 该用户 24 小时内是否已浏览过该问题（浏览量去重） */
    public static String questionViewed(Long questionId, Long userId) {
        return "question:viewed:" + questionId + ":" + userId;
    }

    /** 某用户对某类目标的已点赞集合，targetType 见 {@link LikeTarget} */
    public static String likeSet(Long userId, int targetType) {
        return "like:user:" + userId + ":" + targetType;
    }
}
