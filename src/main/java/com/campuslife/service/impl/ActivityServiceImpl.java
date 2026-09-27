package com.campuslife.service.impl;

import com.campuslife.domain.dto.ActivityFormDTO;
import com.campuslife.domain.dto.ActivityPageQuery;
import com.campuslife.domain.vo.ActivityVO;
import com.campuslife.mapper.ActivityMapper;
import com.campuslife.service.IActivityService;
import com.campuslife.domain.dto.PageDTO;
import com.campuslife.mapper.SignupMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActivityServiceImpl implements IActivityService {

    private final ActivityMapper activityMapper;
    private final SignupMapper signupMapper;
    private final StringRedisTemplate redisTemplate;

    // ============ 以下为核心业务（面试深挖区），由你实现，依赖已注入 ============

    @Override
    public Long publish(ActivityFormDTO form, Long publisherId) {
        // TODO 由你实现：校验时间链（signupStart < signupEnd <= startTime < endTime）→ insert status=1 → SET signup:stock:{id}=quota
        throw new UnsupportedOperationException("TODO: publish 由你实现");
    }

    @Override
    public PageDTO<ActivityVO> queryActivityPage(ActivityPageQuery query) {
        // TODO 由你实现：keyword 模糊 + status 精确 + createTime 倒序分页；status=1 的读 signup:stock:{id} 填 remainingQuota（无 key 视为 quota）
        throw new UnsupportedOperationException("TODO: queryActivityPage 由你实现");
    }

    @Override
    public ActivityVO queryActivityById(Long id, Long userId) {
        // TODO 由你实现：查库（不存在抛"活动不存在"）→ remainingQuota → userId 非空时按 tb_signup 补 mySignupStatus（1/2，否则 null）
        throw new UnsupportedOperationException("TODO: queryActivityById 由你实现");
    }

    @Override
    public void cancelActivity(Long id, Long publisherId) {
        // TODO 由你实现：校验归属 → status=4（已取消幂等返回）；报名中的活动把已报名 tb_signup 1→3 并 DEL users/stock/waitlist 三个 key
        // 通知已报名用户（tb_notification）由你决定是否做
        throw new UnsupportedOperationException("TODO: cancelActivity 由你实现");
    }
}
