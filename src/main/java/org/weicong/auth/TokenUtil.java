package org.weicong.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;
import org.weicong.auth.config.CustomUser;
import org.weicong.common.constant.RpEnum;
import org.weicong.common.exception.BizException;

/**
 * @description 
 * @author weicong
 * @date 2020年1月14日
 * @version 1.0
 */
public abstract class TokenUtil {

	public static String obtainLoginId() {
		CustomUser customUser = obtainLoginUser();
		return customUser.getId();
	}
	
	/**
	 * 获取当前登录用户
	 * @return
	 */
	public static CustomUser obtainLoginUser() {
		Object object = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		if (null == object) {
			throw new BizException(RpEnum.ERROR_SYSTEM);
		}
		if (!(object instanceof CustomUser)) {
			throw new BizException("AnonymousUser can not obtain user info");
		}
		
		return (CustomUser)object;
	}
	
	
	public static void responseCrossOrigin(HttpServletResponse response, HttpServletRequest request) {
		response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Methods", "GET, HEAD, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Max-Age", "180000");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Headers", "Accept, Origin, X-Requested-With, Content-Type,Last-Modified,device,token");
	}
}
