package com.campuslife.controller;

import com.campuslife.common.Result;
import com.campuslife.common.interceptor.UserContext;
import com.campuslife.service.ICheckinService;
import com.campuslife.domain.vo.CheckinVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/checkins")
@RequiredArgsConstructor
public class CheckinController {

    private final ICheckinService checkinService;

    /** 今日签到（登录；Redis Bitmap 深挖区） */
    @PostMapping
    public Result<CheckinVO> checkin() {
        return Result.success(checkinService.checkin(UserContext.getUser()));
    }

    /** 我的签到状态（登录，不签到只查询） */
    @GetMapping("/mine")
    public Result<CheckinVO> mine() {
        return Result.success(checkinService.queryMine(UserContext.getUser()));
    }
}
