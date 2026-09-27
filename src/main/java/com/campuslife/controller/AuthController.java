package com.campuslife.controller;

import com.campuslife.common.Result;
import com.campuslife.domain.dto.LoginFormDTO;
import com.campuslife.domain.dto.RegisterFormDTO;
import com.campuslife.domain.vo.LoginVO;
import com.campuslife.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IUserService userService;

    /** 注册（免登录） */
    @PostMapping("/register")
    public Result<Void> register(@RequestBody @Valid RegisterFormDTO form) {
        userService.register(form);
        return Result.success();
    }

    /** 登录（免登录），返回 token 与用户信息 */
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody @Valid LoginFormDTO form) {
        return Result.success(userService.login(form));
    }
}
