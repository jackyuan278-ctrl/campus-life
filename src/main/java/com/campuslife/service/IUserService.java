package com.campuslife.service;

import com.campuslife.domain.dto.LoginFormDTO;
import com.campuslife.domain.dto.RegisterFormDTO;
import com.campuslife.domain.vo.LoginVO;
import com.campuslife.domain.vo.UserVO;

public interface IUserService {

    /**
     * 注册：校验用户名唯一（重复抛 BizException）→ BCrypt 加密密码（注入 PasswordEncoder）→ 入库
     */
    void register(RegisterFormDTO form);

    /**
     * 登录：查用户 → passwordEncoder.matches 校验 → JwtTool.createToken 签发 → 返回 LoginVO；
     * 用户不存在/密码错误统一抛 BizException("用户名或密码错误")，避免暴露账号是否存在
     */
    LoginVO login(LoginFormDTO form);

    /**
     * 当前登录用户信息（userId 来自 UserContext）
     */
    UserVO queryMe(Long userId);
}
