package com.campuslife.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campuslife.common.BizException;
import com.campuslife.common.constant.RedisKeys;
import com.campuslife.domain.po.Activity;
import com.campuslife.domain.po.Signup;
import com.campuslife.mapper.ActivityMapper;
import com.campuslife.domain.dto.SignupFormDTO;
import com.campuslife.domain.vo.SignupVO;
import com.campuslife.mapper.SignupMapper;
import com.campuslife.service.ISignupService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SignupServiceImpl extends ServiceImpl<SignupMapper, Signup> implements ISignupService {

    private final ActivityMapper activityMapper;
    private final StringRedisTemplate stringRedisTemplate;
    // 两个 Bean 按字段名注入（signupScript/cancelSignupScript），依赖 -parameters 编译参数
    private final DefaultRedisScript<Long> signupScript;
    private final DefaultRedisScript<String> cancelSignupScript;



    @Override
    public void signup(SignupFormDTO form, Long userId) {
        Long activityId = form.getActivityId();
        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) {
            throw new BizException(400,"活动不存在");
        }
        Integer status = activity.getStatus();
        if (status != 1) {
            throw new BizException(400,"活动不在报名时间");
        }
        List<String> signList = new ArrayList<>();
        signList.add(RedisKeys.signupStock(activityId));
        signList.add(RedisKeys.signupUsers(activityId));
        Long result = stringRedisTemplate.execute(signupScript, signList, userId.toString(), activity.getQuota().toString());
        if (result == 0) {
            boolean updated = lambdaUpdate().eq(Signup::getActivityId, activityId).set(Signup::getStatus, 1).eq(Signup::getUserId, userId).update();
            if (!updated) {
                save(new Signup().setActivityId(activityId).setUserId(userId).setStatus(1));
            }
        }
        if (result == 1) {
            throw new BizException(400,"名额已满，可加入候补");
        }
        if (result == 2) {
            throw new BizException(400,"请勿重复报名");
        }
    }

    @Override
    public void joinWaitlist(SignupFormDTO form, Long userId) {
        Long activityId = form.getActivityId();
        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) {
            throw new BizException(400,"活动不存在");
        }
        Integer status = activity.getStatus();
        if (status != 1) {
            throw new BizException(400,"活动不在报名时间");
        }
        Signup one = lambdaQuery().eq(Signup::getUserId, userId).eq(Signup::getActivityId, activityId).one();
        if (one == null) {
            try {
                save(new Signup().setActivityId(activityId).setUserId(userId).setStatus(2));
            } catch (DuplicateKeyException e) {
                throw new BizException(400,"请勿重复加入");
            }
        } else if (one.getStatus() == 3) {
            lambdaUpdate().eq(Signup::getId, one.getId()).set(Signup::getStatus, 2).update();
        } else {
            throw new BizException(400,"已报名或已在候补中，请勿重复加入");
        }

        stringRedisTemplate.opsForList().rightPush(RedisKeys.signupWaitlist(activityId),userId.toString());
    }

    @Override
    public void cancel(Long activityId, Long userId) {
        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) {
            throw new BizException(400,"活动不存在");
        }
        Integer status = activity.getStatus();
        if (status != 1) {
            throw new BizException(400,"活动不在报名时间");
        }
        Signup one = lambdaQuery().eq(Signup::getUserId, userId).eq(Signup::getActivityId, activityId).one();
        if (one == null) {
            throw new BizException(400,"未报名");
        }
        if (one.getStatus() == 2) {
            stringRedisTemplate.opsForList().remove(RedisKeys.signupWaitlist(activityId), 1, userId.toString());
            lambdaUpdate().eq(Signup::getId, one.getId()).set(Signup::getStatus, 3).update();
            return;
        }

        if (one.getStatus() !=1) {
            throw new BizException(400,"未报名");
        }


        List<String> signupList = new ArrayList<>();
        signupList.add(RedisKeys.signupStock(activityId));
        signupList.add(RedisKeys.signupUsers(activityId));
        signupList.add(RedisKeys.signupWaitlist(activityId));
        String result = stringRedisTemplate.execute(cancelSignupScript, signupList, userId.toString());
        if (result.equals("-1")) {
            throw new BizException(400,"未报名");
        }else if (result.equals("0")) {
            lambdaUpdate().eq(Signup::getUserId, userId).eq(Signup::getActivityId, activityId).set(Signup::getStatus,3).update();
        }else{
            lambdaUpdate().eq(Signup::getUserId, userId).eq(Signup::getActivityId, activityId).set(Signup::getStatus,3).update();
            lambdaUpdate().eq(Signup::getUserId, Long.valueOf(result)).eq(Signup::getActivityId, activityId).set(Signup::getStatus,1).update();
        }
    }

    @Override
    public List<SignupVO> queryMine(Long userId) {
        List<Signup> list = lambdaQuery().in(Signup::getStatus, 1, 2).eq(Signup::getUserId, userId).list();
        if (list.isEmpty()) {
            return List.of();
        }
        List<SignupVO> signupVOList = new ArrayList<>();
        List<Long> activityIds = new ArrayList<>();
        for (Signup signup : list) {
            activityIds.add(signup.getActivityId());
        }
        Map<Long, Activity> activityMap = activityMapper.selectByIds(activityIds).stream().collect(Collectors.toMap(a -> a.getId(), a -> a));
        for (Signup signup : list) {
            SignupVO signupVO = BeanUtil.copyProperties(signup, SignupVO.class);
            Activity act = activityMap.get(signup.getActivityId());
            if (act != null) {
                signupVO.setActivityTitle(act.getTitle());
                signupVO.setCoverUrl(act.getCoverUrl());
                signupVO.setLocation(act.getLocation());
                signupVO.setStartTime(act.getStartTime());
            }
            if (signupVO.getStatus().equals(2)) {
                Long range = stringRedisTemplate.opsForList().indexOf(RedisKeys.signupWaitlist(signup.getActivityId()), userId.toString());
                if (range != null) {
                    signupVO.setWaitPosition(range.intValue() + 1);
                }
            }
            signupVOList.add(signupVO);
        }

        return signupVOList;
    }
}
