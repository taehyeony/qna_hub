package com.taehyeon.qna.config;

import com.taehyeon.qna.dto.response.ApiResponse;
import com.taehyeon.qna.enums.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request);

        if(token == null){
            filterChain.doFilter(request,response);
            return;
        }

        if(jwtProvider.validateToken(token)){
            UUID userId = jwtProvider.getSubject(token);
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            filterChain.doFilter(request,response);
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");

            String json = "{" +
                    "\"success\": false, " +
                    "\"status\": 401, " +
                    "\"message\": \"토큰이 만료되었거나 유효하지 않습니다.\", " +
                    "\"errorCode\": \"INVALID_TOKEN\"" +
                    "}";

            response.getWriter().write(json);
        }
    }

    /**
     * 헤더에서 "Authorization": "Bearer [토큰]" 형태에서 토큰 값을 추출
      * @param request HttpServletRequest 개체
     * @return jwt 토큰 문자열
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
