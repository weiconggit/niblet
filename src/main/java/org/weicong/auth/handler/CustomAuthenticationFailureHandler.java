package org.weicong.auth.handler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.weicong.auth.TokenUtil;
import org.weicong.common.constant.Rp;
import org.weicong.common.constant.RpEnum;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * @description 
 * @author weicong
 * @date 2020年1月14日
 * @version 1.0
 */
@Slf4j
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

	private final ObjectMapper objectMapper = new ObjectMapper();
	
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		log.info("认证失败 url [{}]", request.getRequestURL());
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);// 401 认证失败
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
		
		TokenUtil.responseCrossOrigin(response, request);
		
		response.getWriter().write(
				objectMapper.writeValueAsString(
						Rp.build(RpEnum.USERNAME_OR_PASSWORD_ERROR)));
	}

}
