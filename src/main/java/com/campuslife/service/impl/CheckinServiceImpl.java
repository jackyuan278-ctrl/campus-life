package com.campuslife.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campuslife.domain.po.Checkin;
import com.campuslife.mapper.CheckinMapper;
import com.campuslife.service.ICheckinService;
import com.campuslife.domain.vo.CheckinVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 实现留白：核心业务由你实现（ICheckinService 的 javadoc 即契约）。
 * 提示：Bitmap 用 stringRedisTemplate.opsForValue().setBit/getBit，key 形如 checkin:sign:2026-09
 */
@Service
@RequiredArgsConstructor
public class CheckinServiceImpl extends ServiceImpl<CheckinMapper, Checkin> implements ICheckinService {

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public CheckinVO checkin(Long userId) {
        // TODO 由你实现：GETBIT checkin:sign:{yyyy-MM} (当月日-1) 已签则抛"今日已签到" → SETBIT + insert tb_checkin → 回数连续天数
        // 回数跨月要换成上一个月的 key；streak 与本月日期列表建议抽私有方法，queryMine 复用
        throw new UnsupportedOperationException("TODO: checkin 由你实现");
    }

    @Override
    public CheckinVO queryMine(Long userId) {
        // TODO 由你实现：checkin 的只读版——GETBIT 判今天 + 回数 streak + 遍历本月收集 checkinDates，不写任何数据
        throw new UnsupportedOperationException("TODO: queryMine 由你实现");
    }
}
