package org.weicong.auth.abandon;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @description 自定义认证管理器
 * @author weicong
 * @date 2019年12月17日
 * @version 1.0
 */
@Slf4j
@AllArgsConstructor
@Deprecated
public class TokenAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService customUserDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        System.out.println("provider执行");
    	CustomAuthenticationToken authenticationToken = (CustomAuthenticationToken) authentication;
    	if (null == authenticationToken) {
    		log.info("authenticationToken is null");
			throw new UsernameNotFoundException("authenticationToken is null");
		}
    	
        UserDetails userDetails = customUserDetailsService.loadUserByUsername((String) authenticationToken.getPrincipal());
        if (userDetails == null) {
        	log.info("username not found");
            throw new UsernameNotFoundException("unable to obtain user information");
        }
        
        if (!authentication.getCredentials().equals(userDetails.getPassword())) {
        	log.info("username or password error");
        	throw new UsernameNotFoundException("username or password error");
        }
        
        // 将密码置空
        CustomAuthenticationToken authenticationResult = new CustomAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        return authenticationResult;
    }

    /**
     * 根据token类型，来判断使用哪个Provider
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return CustomAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
