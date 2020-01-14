package org.weicong.auth.handler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.weicong.auth.TokenUtil;
import org.weicong.common.constant.Rp;
import org.weicong.common.constant.RpEnum;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * @description 认证
 * @author weicong
 * @date 2020年1月14日
 * @version 1.0
 */
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private final ObjectMapper objectMapper = new ObjectMapper();
	private boolean isCustom;
	private RpEnum rpEnum;
	
	public CustomAuthenticationEntryPoint() {}
	
	public CustomAuthenticationEntryPoint(boolean isCustom, RpEnum rpEnum) {
		this.isCustom = isCustom;
		this.setRpEnum(rpEnum);
	}
	
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		log.info("认证入口处理器 url [{}]", request.getRequestURL());
		
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);// 401 未认证
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
		
		TokenUtil.responseCrossOrigin(response, request);
		
		response.getWriter().write(
				isCustom ? 
				objectMapper.writeValueAsString(Rp.build(rpEnum, authException.getMessage()))
				:
				objectMapper.writeValueAsString(Rp.build(RpEnum.NO_AUTHEN))
		);
		
	}

	public void setRpEnum(RpEnum rpEnum) {
		this.rpEnum = rpEnum;
	}

}
