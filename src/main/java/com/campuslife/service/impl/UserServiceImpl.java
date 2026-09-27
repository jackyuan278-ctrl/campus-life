package com.campuslife.service.impl;

import com.campuslife.common.utils.JwtTool;
import com.campuslife.domain.dto.LoginFormDTO;
import com.campuslife.domain.dto.RegisterFormDTO;
import com.campuslife.domain.vo.LoginVO;
import com.campuslife.domain.vo.UserVO;
import com.campuslife.mapper.UserMapper;
import com.campuslife.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTool jwtTool;

    // ============ 以下为核心业务（面试深挖区），由你实现，依赖已注入 ============

    @Override
    public void register(RegisterFormDTO form) {
        // TODO 由你实现：用户名查重（重复抛 BizException）→ passwordEncoder.encode → insert
        throw new UnsupportedOperationException("TODO: register 由你实现");
    }

    @Override
    public LoginVO login(LoginFormDTO form) {
        // TODO 由你实现：查用户 → passwordEncoder.matches → jwtTool.createToken
        // 用户不存在与密码错误用同一文案"用户名或密码错误"，防账号枚举
        throw new UnsupportedOperationException("TODO: login 由你实现");
    }

    @Override
    public UserVO queryMe(Long userId) {
        // TODO 由你实现：selectById → BeanUtil.copyProperties(user, UserVO.class)
        throw new UnsupportedOperationException("TODO: queryMe 由你实现");
    }
}
