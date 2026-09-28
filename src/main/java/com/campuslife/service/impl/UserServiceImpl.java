package com.campuslife.service.impl;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campuslife.common.BizException;
import com.campuslife.common.utils.JwtTool;
import com.campuslife.domain.dto.LoginFormDTO;
import com.campuslife.domain.dto.RegisterFormDTO;
import com.campuslife.domain.po.User;
import com.campuslife.domain.vo.LoginVO;
import com.campuslife.domain.vo.UserVO;
import com.campuslife.mapper.UserMapper;
import com.campuslife.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private final PasswordEncoder passwordEncoder;
    private final JwtTool jwtTool;

    // ============ 以下为核心业务（面试深挖区），由你实现，依赖已注入 ============

    @Override
    public void register(RegisterFormDTO form) {
        // TODO 由你实现：用户名查重（重复抛 BizException）→ passwordEncoder.encode → insert
        String username = form.getUsername();
        boolean exists = lambdaQuery().eq(User::getUsername, username).exists();
        if (exists) {
            throw new BizException(400,"用户名已存在");
        }
        String password = form.getPassword();
        String nickname = form.getNickname();
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setNickname(nickname);
        user.setCreateTime(LocalDateTime.now());
        save(user);
    }

    @Override
    public LoginVO login(LoginFormDTO form) {
        // TODO 由你实现：查用户 → passwordEncoder.matches → jwtTool.createToken
        // 用户不存在与密码错误用同一文案"用户名或密码错误"，防账号枚举
        String password = form.getPassword();
        String username = form.getUsername();
        User user = lambdaQuery().eq(User::getUsername, username).one();
        if (user == null) {
            throw new BizException(400,"用户名或密码错误");
        }
        boolean matches = passwordEncoder.matches(form.getPassword(), user.getPassword());
        if (!matches) {
            throw new BizException(400,"用户名或密码错误");
        }
        return LoginVO.builder()
                .token(jwtTool.createToken(user.getId()))
                .username(username)
                .userId(user.getId())
                .nickname(user.getNickname())
                .build();
    }

    @Override
    public UserVO queryMe(Long userId) {
        // TODO 由你实现：selectById → BeanUtil.copyProperties(user, UserVO.class)
        User user = lambdaQuery().eq(User::getId, userId).one();
        if (user == null) {
            throw new BizException(400,"用户不存在");
        }
        return BeanUtil.copyProperties(user, UserVO.class);
    }
}
