package com.campuslife.controller;

import com.campuslife.common.Result;
import com.campuslife.common.interceptor.UserContext;
import com.campuslife.domain.dto.SignupFormDTO;
import com.campuslife.domain.vo.SignupVO;
import com.campuslife.service.ISignupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/signups")
@RequiredArgsConstructor
public class SignupController {

    private final ISignupService signupService;

    /** 报名（登录） */
    @PostMapping
    public Result<Void> signup(@RequestBody @Valid SignupFormDTO form) {
        signupService.signup(form, UserContext.getUser());
        return Result.success();
    }

    /** 加入候补（登录） */
    @PostMapping("/waitlist")
    public Result<Void> joinWaitlist(@RequestBody @Valid SignupFormDTO form) {
        signupService.joinWaitlist(form, UserContext.getUser());
        return Result.success();
    }

    /** 取消报名/候补（登录） */
    @DeleteMapping("/{activityId}")
    public Result<Void> cancel(@PathVariable("activityId") Long activityId) {
        signupService.cancel(activityId, UserContext.getUser());
        return Result.success();
    }

    /** 我的报名+候补列表（登录） */
    @GetMapping("/mine")
    public Result<List<SignupVO>> mine() {
        return Result.success(signupService.queryMine(UserContext.getUser()));
    }
}
