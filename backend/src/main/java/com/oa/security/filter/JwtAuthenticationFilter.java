package com.oa.security.filter;

import com.oa.security.jwt.JwtProperties;
import com.oa.security.jwt.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT 认证过滤器
 * 从请求头提取 Token，验证后设置 SecurityContext
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final JwtProperties jwtProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 获取 Authorization 请求头
        String authHeader = request.getHeader(jwtProperties.getHeader());

        // 提取 Token（去除前缀）
        String token = jwtUtils.extractToken(authHeader);

        // 验证 Token 并设置认证信息
        if (StringUtils.hasText(token) && jwtUtils.validateToken(token)) {
            Claims claims = jwtUtils.parseToken(token);
            if (claims != null) {
                Long userId = claims.get("userId", Long.class);
                String username = claims.getSubject();

                // 创建认证对象
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                        userId,      // principal：存储用户ID
                        null,        // credentials：不需要密码
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                    );

                // 设置到 SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("JWT 认证成功: userId={}, username={}", userId, username);
            }
        }

        // 继续过滤器链
        filterChain.doFilter(request, response);
    }
}