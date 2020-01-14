package org.weicong.auth.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

/**
 * @description 从数据库加载用户信息
 * @author weicong
 * @date 2020年1月14日
 * @version 1.0
 */
@AllArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {
	
//	private final ISysUserService sysUserService;
//	private final WebSecurityConfigProperties webProperties;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//		SysUser sysUser = sysUserService.getUserByUsername(username);
//		
//		if (null == sysUser) {
//			return new CustomUser(username, "", new ArrayList<>());
//		}
//		
//		if (webProperties.getSysadmin().equals(username)) {
//			return new CustomUser(sysUser.getId(), new ArrayList<>(), sysUser.getUsername(), sysUser.getPassword(), new ArrayList<>());
//		}
//		
//		// TODO 
//		List<String> urList = new ArrayList<>();
//		urList.add("GET/auth/{v}");
//		urList.add("GET/obtain");
//		List<SimpleGrantedAuthority> roles = new ArrayList<>();
//		
//		return new CustomUser(sysUser.getId(), urList, sysUser.getUsername(), sysUser.getPassword(), roles);	
		
		
		// 测试用==========================================
		List<String> uriList = new ArrayList<>();
		uriList.add("GET/auth/{v}");
		uriList.add("GET/obtain");

		List<SimpleGrantedAuthority> roles = new ArrayList<>();
		return new CustomUser("11111", "weicong", "123456", uriList,roles);
	}
}
