package org.weicong.auth.abandon;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import lombok.Data;

/**
 * @description 自定义投票器
 * @author weicong
 * @date 2019年8月28日
 * @version 1.0
 */
@Deprecated
@Data
public class TokenAccessDecisionVoter implements AccessDecisionVoter<Object> {

	// ~ Instance fields
	// ================================================================================================

	private String rolePrefix = "URI_";

	// ~ Methods
	// ========================================================================================================

	@Override
	public boolean supports(ConfigAttribute attribute) {
		return true;
//		if ((attribute.getAttribute() != null)
//				&& attribute.getAttribute().startsWith(getRolePrefix())) {
//			return true;
//		}
//		else {
//			return false;
//		}
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return true;
	}

	@Override
	public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {
		String uasername = SecurityContextHolder.getContext().getAuthentication().getName();
		System.out.println("Voter执行1" + uasername);

		int result = ACCESS_DENIED; // 访问拒绝
		if (authentication == null)
			return result;
		
		System.out.println("Voter执行2" + uasername);

		FilterInvocation fi = (FilterInvocation) object;
		HttpServletRequest request = fi.getRequest();

		AntPathRequestMatcher antPathRequestMatcher = new AntPathRequestMatcher("/auth/**", "GET");

		// 这里可以获取到该用户将要访问的api资源的url信息
		String url = fi.getRequestUrl();
		System.err.println(url);
		for (ConfigAttribute configAttribute : attributes) {
			if (antPathRequestMatcher.matches(request)) {
				result = ACCESS_GRANTED;
			}
		}
		return result;
	}

}
