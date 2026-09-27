package com.campuslife.common.config;

import com.campuslife.common.interceptor.UserInfoInterceptor;
import com.campuslife.common.utils.JwtTool;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class MvcConfig implements WebMvcConfigurer {

    private final JwtTool jwtTool;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 免登录：注册/登录、健康检查；GET /activities/** 在拦截器内部放行（excludePathPatterns 不支持按方法区分）
        registry.addInterceptor(new UserInfoInterceptor(jwtTool))
                .addPathPatterns("/**")
                .excludePathPatterns("/auth/**", "/health", "/error");
    }
}
