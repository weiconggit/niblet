package org.weicong.auth.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.frameoptions.WhiteListedAllowFromStrategy;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.weicong.auth.abandon.TokenAccessDecisionVoter;
import org.weicong.auth.handler.CustomAccessDeniedHandler;
import org.weicong.auth.handler.CustomAuthenticationEntryPoint;
import org.weicong.auth.handler.CustomAuthenticationFailureHandler;
import org.weicong.auth.handler.CustomAuthenticationSuccessHandler;
import org.weicong.common.thread.AsyncService;
import org.weicong.common.util.RedisUtil;

import lombok.AllArgsConstructor;

/**
 * @description
 * @author weicong
 * @date 2020年1月14日
 * @version 1.0
 */
@AllArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	// ~ Final fields
	// ====================================================================================
	
	private final CustomUserDetailsService customUserDetailsService;
	private final RedisUtil redisUtil;
	private final RequestMappingHandlerMapping handlerMapping;
	private final WebSecurityConfigProperties webProperties;
	private final AsyncService asyncService;
	
	// ~ Main Methods
	// ====================================================================================
	
	// @formatter:off
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// use custom decision manager with code [.accessDecisionManager(accessDecisionManager())]					
		// need spring security to protect, use [.antMatchers(obtainIgnoreUris()).permitAll()] and [.anyRequest().authenticated()] 
		// use custom provider with code [.authenticationProvider(new TokenAuthenticationProvider(customUserDetailsService))] behind code [.addFilterAt...]
		
		http	
			.sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
	    .and()
	    	.authorizeRequests()
	    	.antMatchers(HttpMethod.OPTIONS).permitAll()
	    	.anyRequest().permitAll()
        .and()
        	.headers().frameOptions().disable()
        .and()
        	.headers().addHeaderWriter(new XFrameOptionsHeaderWriter(
        					new WhiteListedAllowFromStrategy(Arrays.asList("http://www.xxx.cn"))))
        .and()
			.csrf().disable()
        	.addFilterBefore(new TokenAuthorityFilter(redisUtil, webProperties, handlerMapping, asyncService), UsernamePasswordAuthenticationFilter.class)
	        .addFilterAt(obtainTokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
        .exceptionHandling()
			.authenticationEntryPoint(new CustomAuthenticationEntryPoint())
			.accessDeniedHandler(new CustomAccessDeniedHandler());
	}	
	// @formatter:on

	/**
	 * 获取自定义认证过滤
	 * @return
	 * @throws Exception
	 */
	private TokenAuthenticationFilter obtainTokenAuthenticationFilter() throws Exception {
		TokenAuthenticationFilter tokenFilter = new TokenAuthenticationFilter();
		tokenFilter.setAuthenticationManager(authenticationManagerBean());
		tokenFilter.setAuthenticationFailureHandler(new CustomAuthenticationFailureHandler());
		tokenFilter.setAuthenticationSuccessHandler(new CustomAuthenticationSuccessHandler(redisUtil));
		return tokenFilter;
	}

	@Bean
	public static PasswordEncoder passwordEncoder() {
		// use [NoOpPasswordEncoder.getInstance();] to test
//		return new BCryptPasswordEncoder(); // normal
		return NoOpPasswordEncoder.getInstance(); // test
	}

	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder());
	}

	// ~ Mode 2 使用自定义投票器进行动态uri鉴权
	// ====================================================================================
	/**
	 * 自定义决策器，配合自定义投票器
	 * @return
	 */
	@Deprecated
	public AccessDecisionManager accessDecisionManager() {
		List<AccessDecisionVoter<? extends Object>> decisionVoters = Arrays.asList(
				new WebExpressionVoter(),
				new RoleVoter(),
				new TokenAccessDecisionVoter(),
				new AuthenticatedVoter());
		return new UnanimousBased(decisionVoters);
	}
	
	// ~ Tool Methods
	// ====================================================================================
	
	/**
	 * obtain the uris of spring security which do not need to auth
	 * @return
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private String[] obtainIgnoreUris() {
		List<String> ignoreRestFulUris = webProperties.getExcludePath();
		List<String> ignoreUris = new ArrayList<>(ignoreRestFulUris.size());
		for (String restUri : ignoreRestFulUris) {
			ignoreUris.add(restUri.substring(3));
		}
		String[] uris = new String[ignoreRestFulUris.size()];
		ignoreUris.toArray(uris);
		return uris;
	}

}
