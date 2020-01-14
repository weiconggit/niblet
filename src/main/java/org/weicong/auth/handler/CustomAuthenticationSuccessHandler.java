package org.weicong.auth.handler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.weicong.auth.TokenUtil;
import org.weicong.auth.config.CustomUser;
import org.weicong.common.constant.Rp;
import org.weicong.common.util.RedisUtil;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * @description 
 * @author weicong
 * @date 2020年1月14日
 * @version 1.0
 */
@Slf4j
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler{

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final RedisUtil redisUtil;
	
	public CustomAuthenticationSuccessHandler(RedisUtil redisUtil) {
		this.redisUtil = redisUtil;
	}
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		CustomUser cdbooksUser = (CustomUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String token = generateToken();
        redisUtil.set(token, cdbooksUser, RedisUtil.USER_EXP);
		
		SecurityContextHolder.clearContext();	
		log.info("认证成功 username [{}]", cdbooksUser.getUsername());
		response.setStatus(HttpServletResponse.SC_OK);// 200 服务请求成功
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
		
		TokenUtil.responseCrossOrigin(response, request);
		
		response.getWriter().write(
				objectMapper.writeValueAsString(
						Rp.buildSuccess(token)));
	}

	private String generateToken() {
		return UUID.randomUUID().toString().toLowerCase();
	}
}
