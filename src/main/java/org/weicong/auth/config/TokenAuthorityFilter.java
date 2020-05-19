package org.weicong.auth.config;

import java.io.IOException;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.weicong.auth.handler.CustomAccessDeniedHandler;
import org.weicong.auth.handler.CustomAuthenticationEntryPoint;
import org.weicong.common.constant.RpEnum;
import org.weicong.common.thread.AsyncService;
import org.weicong.common.util.RedisUtil;
import org.weicong.common.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @description 自定义过滤器，权限相关的主要逻辑处理（权限校验，用户信息封装到SpringSecurityContext等）
 * @author weicong
 * @date 2020年1月14日
 * @version 1.0
 */
@Slf4j
public class TokenAuthorityFilter extends OncePerRequestFilter {

	// ~ Static fields/initializers
	// =====================================================================================

	private static final String AUTHORIZATION = "Authorization";
	
	@SuppressWarnings("unused")
	@Deprecated
	private static final String BEARER = "Bearer";

	private RedisUtil redisUtil;
	private RequestMappingHandlerMapping handlerMapping;
	private AntPathMatcher antMatcher = new AntPathMatcher();
	private WebSecurityConfigProperties webProperties;
	private List<String> excludePath;
	private AsyncService asyncService;

	// ~ Constructors
	// =======================================================================================

	public TokenAuthorityFilter(RedisUtil redisUtil, WebSecurityConfigProperties webProperties,
			RequestMappingHandlerMapping handlerMapping, AsyncService asyncService) {
		this.redisUtil = redisUtil;
		this.webProperties = webProperties;
		this.handlerMapping = handlerMapping;	
		this.excludePath = webProperties.getExcludePath();
		this.asyncService = asyncService;
	}

	// ~ Main Method
	// =======================================================================================

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {	
		if (request.getMethod().equals(RequestMethod.OPTIONS.toString())) {
			filterChain.doFilter(request, response);
			return;
		}
		
		if (preFilter(request)) {
			filterChain.doFilter(request, response);
			return;
		}

		String token = request.getHeader(AUTHORIZATION);
		log.debug("current uri is [{}], token is [{}]", request.getRequestURI(), token);
		
		if (StringUtil.isEmpty(token)) {
			new CustomAuthenticationEntryPoint().commence(request, response, new AuthenticationServiceException("token is empty"));
			return;
		} 

		CustomUser customUser = obtainUser(token);
		log.debug("customUser is [{}]", customUser);
		
		if (null == customUser) {
			new CustomAuthenticationEntryPoint(true, RpEnum.NO_PERMIT).commence(request, response, new AuthenticationServiceException("bad token"));
			return;
		}
		
		if (customUser.getUsername().equals(webProperties.getSysadmin())) {
			setSecurityContext(customUser, request);
			filterChain.doFilter(request, response);
			return;
		}

		log.debug("customUser.getUrlList is [{}]", customUser.getUrlList());
		if (!checkPermission(customUser.getUrlList(), obtainUri(request, response))) {
			throwAuthorException("access denied", request, response);
			return;
		}

		setSecurityContext(customUser, request);
		filterChain.doFilter(request, response);

	}

	// ~ Tool Methods
	// =======================================================================================

	/**
	 * 使用自定义处理器处理返回信息
	 * 
	 * @param msg
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws ServletException
	 */
	private void throwAuthorException(String msg, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		new CustomAccessDeniedHandler().handle(request, response, new AccessDeniedException(msg));
	}

	/**
	 * 获取解析后的uri
	 * 
	 * @param request
	 * @return
	 * @throws ServletException 
	 * @throws IOException 
	 */
	private String obtainUri(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		try {
			handlerMapping.getHandler(request);
		} catch (Exception e) {
			log.error("init handlerMapping wrong : [{}]", e);
			throwAuthorException(e.getMessage(), request, response);
		}
		
		String currentUri = String.valueOf(request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE));
		return new StringBuilder(request.getMethod()).append(currentUri).toString();
	}

	/**
	 * 获取用户信息
	 * 
	 * @param token
	 * @return
	 */
	private CustomUser obtainUser(String token) {
		Object object = redisUtil.get(token);
		
		if (null == object) {
			return null;
		}
		
		if (!(object instanceof CustomUser)) {
			return null;
		}
		
		asyncService.execute(()-> {
			if (redisUtil.getExpire(token) < RedisUtil.REFRESH_THRESHOLD) {
				redisUtil.expire(token, RedisUtil.USER_EXP);
			}
		});
		
		return (CustomUser) object;
	}

	/**
	 * 将信息交还于SpringSecurity
	 * 
	 * @param customUser
	 * @param request
	 */
	private void setSecurityContext(CustomUser customUser, HttpServletRequest request) {
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(customUser, null,
				customUser.getAuthorities());
		authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		// useless 
		authentication = null;
	}

	/**
	 * 权限校验
	 * 
	 * @param userUris
	 * @param currentUri
	 * @return
	 */
	private boolean checkPermission(List<String> userUris, String currentUri) {
		log.debug("current path is [{}]", currentUri);
		
		for (int i = 0, size = userUris.size(); i < size; i++) {
			if (currentUri.equals(userUris.get(i))) {
				log.debug("[{}] auth path is true", userUris.get(i));
				return true;
			}
		}
		return false;
	}

	/**
	 * 过滤前置
	 * 
	 * @param uri 当前请求uri
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	private boolean preFilter(HttpServletRequest request) throws ServletException, IOException {
		String uri = request.getMethod() + request.getRequestURI();
		log.debug("current path is [{}]", uri);
		
		for (int i = 0, size = excludePath.size(); i < size; i++) {
			if (antMatcher.match(excludePath.get(i), uri)) {
				log.debug("[{}] exclude path is true", excludePath.get(i));
				return true;
			}
		}
		return false;
	}

}
