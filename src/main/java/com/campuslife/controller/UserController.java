package com.campuslife.controller;

import com.campuslife.common.Result;
import com.campuslife.common.interceptor.UserContext;
import com.campuslife.domain.vo.UserVO;
import com.campuslife.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    /** 当前登录用户信息 */
    @GetMapping("/me")
    public Result<UserVO> me() {
        return Result.success(userService.queryMe(UserContext.getUser()));
    }
}
