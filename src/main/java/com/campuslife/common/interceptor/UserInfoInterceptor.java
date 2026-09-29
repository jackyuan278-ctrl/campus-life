package com.campuslife.common.interceptor;

import com.campuslife.common.BizException;
import com.campuslife.common.utils.JwtTool;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 单体应用自校验 JWT（不同于 zgmall 网关透传 user-info）：
 * 解析 Authorization: Bearer xxx 成功后把 userId 放入 UserContext
 */
public class UserInfoInterceptor implements HandlerInterceptor {

    private final JwtTool jwtTool;

    public UserInfoInterceptor(JwtTool jwtTool) {
        this.jwtTool = jwtTool;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 活动与问答浏览免登录（带 token 时详情会附带我的报名/点赞状态）
        if ("GET".equalsIgnoreCase(request.getMethod())
                && (request.getRequestURI().startsWith("/activities")
                    || request.getRequestURI().startsWith("/questions")
                    || request.getRequestURI().startsWith("/answers")
                    || "/tags".equals(request.getRequestURI()))) {
            String auth = request.getHeader("Authorization");
            if (StringUtils.hasText(auth) && auth.startsWith("Bearer ")) {
                try {
                    UserContext.setUser(jwtTool.parseToken(auth.substring(7)));
                } catch (JwtException ignored) {
                    // 软认证：token 失效按未登录处理，不拦浏览
                }
            }
            return true;
        }
        String auth = request.getHeader("Authorization");
        if (!StringUtils.hasText(auth) || !auth.startsWith("Bearer ")) {
            throw new BizException(401, "未登录");
        }
        try {
            UserContext.setUser(jwtTool.parseToken(auth.substring(7)));
        } catch (JwtException e) {
            throw new BizException(401, "登录已过期，请重新登录");
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.remove();
    }
}
