package org.weicong.auth.abandon;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @description 
 * @author weicong
 * @date 2019年12月20日
 * @version 1.0
 */
@Deprecated
@Configuration
public class MvnConfig implements WebMvcConfigurer{

	public void addViewControllers(ViewControllerRegistry registry) {
	    registry.addViewController("/home").setViewName("home");
	    registry.addViewController("/").setViewName("home");
	    registry.addViewController("/hello").setViewName("hello");
	    registry.addViewController("/login").setViewName("login");
	}
}
