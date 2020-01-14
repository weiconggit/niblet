package org.weicong.auth.handler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
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
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

	private final ObjectMapper objectMapper = new ObjectMapper();
	
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {
		log.info("访问拒绝处理器 url [{}]", request.getRequestURL());
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);// 401 未授权
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
		
		TokenUtil.responseCrossOrigin(response, request);
		
		response.getWriter().write(
				objectMapper.writeValueAsString(
						Rp.build(RpEnum.NO_AUTHOR, accessDeniedException.getMessage())));
	}

}
