package com.campuslife.service.impl;

import com.campuslife.mapper.ActivityMapper;
import com.campuslife.domain.dto.SignupFormDTO;
import com.campuslife.domain.vo.SignupVO;
import com.campuslife.mapper.SignupMapper;
import com.campuslife.service.ISignupService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SignupServiceImpl implements ISignupService {

    private final SignupMapper signupMapper;
    private final ActivityMapper activityMapper;
    private final StringRedisTemplate redisTemplate;
    // 两个 Bean 按字段名注入（signupScript/cancelSignupScript），依赖 -parameters 编译参数
    private final DefaultRedisScript<Long> signupScript;
    private final DefaultRedisScript<Long> cancelSignupScript;

    // ============ 以下为核心业务（面试深挖区），由你实现，依赖已注入 ============

    @Override
    public void signup(SignupFormDTO form, Long userId) {
        // TODO 由你实现：校验活动报名中 → 执行 signupScript（0成功落库 tb_signup status=1 / 1已满抛"可加入候补" / 2重复抛错）
        // 坑：signup:stock:{activityId} 为 null 时先 SET 成 quota，否则 Lua 会误判"已满"
        throw new UnsupportedOperationException("TODO: signup 由你实现");
    }

    @Override
    public void joinWaitlist(SignupFormDTO form, Long userId) {
        // TODO 由你实现：校验未报名 → tb_signup status=2 → RPUSH signup:waitlist:{activityId}
        throw new UnsupportedOperationException("TODO: joinWaitlist 由你实现");
    }

    @Override
    public void cancel(Long activityId, Long userId) {
        // TODO 由你实现：已报名 → cancelSignupScript（-1未报名/0无补位/>0补位者ID），取消者 1→3，>0 时补位者 2→1
        //             候补中 → LREM signup:waitlist:{activityId} 移除自己 + 2→3
        throw new UnsupportedOperationException("TODO: cancel 由你实现");
    }

    @Override
    public List<SignupVO> queryMine(Long userId) {
        // TODO 由你实现：查 status in(1,2) → selectBatchIds 补活动标题/封面/地点/开始时间 → 候补的按 LRANGE waitlist 下标+1 填 waitPosition
        throw new UnsupportedOperationException("TODO: queryMine 由你实现");
    }
}
