package org.weicong.sys;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.weicong.auth.TokenUtil;
import org.weicong.auth.config.CustomUser;
import org.weicong.common.constant.Rp;
import org.weicong.common.exception.BizException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @description 
 * @author weicong
 * @date 2020年1月14日
 * @version 1.0
 */
@Api(value = "系统检测", tags = "系统检测")
@RestController
public class DetectionEndpoint {

	@ApiOperation(value = "系统存活检测")
	@GetMapping(value = "alive")
	public String alive(HttpServletResponse response) {	
		return String.format("niblet is alive");
	}
	
	@ApiOperation(value = "有权限")
	@GetMapping(value = "auth/{v}")
	public String auth(@PathVariable String v) {
		return String.format("auth is : [%s]", v);
	}
	
	@ApiOperation(value = "无权限")
	@GetMapping(value = "noauth/{v}")
	public String log(@PathVariable String v) {
		return String.format("noauth is : [%s]", v);
	}
	
	@ApiOperation(value = "获取登录用户")
	@GetMapping(value = "obtain")
	public Rp<CustomUser> obtainUser() {		
		return Rp.buildSuccess(TokenUtil.obtainLoginUser());
	}
	
	@ApiOperation(value = "restful拦截测试")
	@GetMapping(value = "rest/{v}")
	public String rest(@PathVariable String v) {
		return String.format("noauth is : [%s]", v);
	}
	
	@ApiOperation(value = "restful拦截测试2")
	@GetMapping(value = "rest/haha/{v}")
	public String rest2(@PathVariable String v) {
		return String.format("noauth is : [%s]", v);
	}
	
	@ApiOperation(value = "restful拦截测试3")
	@PutMapping(value = "rest/{v}")
	public String rest3(@PathVariable String v) {
		return String.format("noauth is : [%s]", v);
	}
	
	@ApiOperation(value = "自定义异常")
	@GetMapping(value = "ex")
	public String ex() {
		throw new BizException("异常信息");
	}
}
