package com.campuslife.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campuslife.common.BizException;
import com.campuslife.common.constant.RedisKeys;
import com.campuslife.domain.dto.ActivityFormDTO;
import com.campuslife.domain.dto.ActivityPageQuery;
import com.campuslife.domain.po.Activity;
import com.campuslife.domain.po.Signup;
import com.campuslife.domain.vo.ActivityVO;
import com.campuslife.mapper.ActivityMapper;
import com.campuslife.service.IActivityService;
import com.campuslife.domain.dto.PageDTO;
import com.campuslife.mapper.SignupMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements IActivityService {

    private final SignupMapper signupMapper;
    private final StringRedisTemplate stringRedisTemplate;

    // ============ 以下为核心业务（面试深挖区），由你实现，依赖已注入 ============

    @Override
    public Long publish(ActivityFormDTO form, Long publisherId) {
        LocalDateTime signupStart = form.getSignupStartTime();
        LocalDateTime signupEnd = form.getSignupEndTime();
        LocalDateTime activityStart = form.getStartTime();
        LocalDateTime activityEnd = form.getEndTime();
        // 时间链：signupStart < signupEnd <= activityStart < activityEnd
        if (!signupStart.isBefore(signupEnd)) {
            throw new BizException(400, "报名开始时间必须早于报名截止时间");
        }
        if (signupEnd.isAfter(activityStart)) {
            throw new BizException(400, "报名截止时间不能晚于活动开始时间");
        }
        if (!activityStart.isBefore(activityEnd)) {
            throw new BizException(400, "活动开始时间必须早于结束时间");
        }
        Activity activity = BeanUtil.copyProperties(form, Activity.class);
        activity.setPublisherId(publisherId);
        activity.setCreateTime(LocalDateTime.now());
        // 发布即报名中；1→2、2→3 的流转归 ActivityStatusTask
        activity.setStatus(1);
        save(activity);
        stringRedisTemplate.opsForValue().set(RedisKeys.signupStock(activity.getId()),
                activity.getQuota().toString());
        return activity.getId();
    }

    @Override
    public PageDTO<ActivityVO> queryActivityPage(ActivityPageQuery query) {
        Integer status = query.getStatus();
        String keyword = query.getKeyword();
        LambdaQueryChainWrapper<Activity> chain = lambdaQuery()
                .eq(status!=null,Activity::getStatus, status)
                .like(StringUtils.hasText(keyword),Activity::getTitle, keyword)
                .orderByDesc(Activity::getCreateTime);
        Page<Activity> page = chain.page(query.toMpPage());
        List<Activity> activityList = page.getRecords();
        PageDTO<ActivityVO> pageDTO = new PageDTO<>();
        List<ActivityVO> activityVOList = new ArrayList<>();
        pageDTO.setTotal(page.getTotal());
        for (Activity activity : activityList) {
            ActivityVO activityVO = BeanUtil.copyProperties(activity, ActivityVO.class);
            if (activity.getStatus().equals(1)) {
                String remain = stringRedisTemplate.opsForValue().get(RedisKeys.signupStock(activity.getId()));
                if (remain == null) {
                    activityVO.setRemainingQuota(activity.getQuota());
                }else{
                    activityVO.setRemainingQuota(Long.valueOf(remain).intValue());
                }
            }
            activityVOList.add(activityVO);

        }
        pageDTO.setList(activityVOList);
        return pageDTO;
    }

    @Override
    public ActivityVO queryActivityById(Long id, Long userId) {
        Activity activity = lambdaQuery().eq(Activity::getId, id).one();
        if (activity == null) {
            throw new BizException(400,"活动不存在");
        }
        ActivityVO activityVO = BeanUtil.copyProperties(activity, ActivityVO.class);
        if (activity.getStatus().equals(1)) {
            String remain = stringRedisTemplate.opsForValue().get(RedisKeys.signupStock(activity.getId()));
            if (remain == null) {
                activityVO.setRemainingQuota(activity.getQuota());
            }else{
                activityVO.setRemainingQuota(Long.valueOf(remain).intValue());
            }
        }
        if (userId!=null){
            Signup signup = signupMapper.selectOne(Wrappers.<Signup>lambdaQuery()
                    .eq(Signup::getUserId, userId)
                    .in(Signup::getStatus, 1, 2)
                    .eq(Signup::getActivityId, activity.getId()));
            if (signup!=null){
                activityVO.setMySignupStatus(signup.getStatus());
            }
        }
        return activityVO;
    }

    @Override
    public void cancelActivity(Long id, Long publisherId) {
        // TODO 由你实现：校验归属 → status=4（已取消幂等返回）；报名中的活动把已报名 tb_signup 1→3 并 DEL users/stock/waitlist 三个 key
        // 通知已报名用户（tb_notification）由你决定是否做
        throw new UnsupportedOperationException("TODO: cancelActivity 由你实现");
    }
}
