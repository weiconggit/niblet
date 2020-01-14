package org.weicong.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @description 跨域
 * @author weicong
 * @date 2020年1月14日
 * @version 1.0
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
			
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedOrigins("*").allowedMethods("POST", "GET", "PUT", "OPTIONS", "DELETE")
				.allowedHeaders("*").maxAge(3600).allowCredentials(true);
	}
	
}
