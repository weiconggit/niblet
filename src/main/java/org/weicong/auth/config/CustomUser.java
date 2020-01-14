package org.weicong.auth.config;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

/**
 * @description 自定义 Spring Security 用户信息，增加id和uri权限信息
 * @author weicong
 * @date 2020年1月14日
 * @version 1.0
 */
public class CustomUser extends User implements Serializable {

	private static final long serialVersionUID = -7041106311015979624L;
	
	private String id;
	private List<String> urlList;
	
	public CustomUser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
		super(username, password, authorities);
	}
	
	public CustomUser(String id, String username, String password, List<String> urlList, Collection<? extends GrantedAuthority> authorities) {
		super(username, password, authorities);
		this.id = id;
		this.urlList = urlList;
	}

	public List<String> getUrlList() {
		return urlList;
	}

	public void setUrlList(List<String> urlList) {
		this.urlList = urlList;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
}
