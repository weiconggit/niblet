package org.weicong.auth.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * @description 
 * @author weicong
 * @date 2020年1月14日
 * @version 1.0
 */
@Data
@Configuration
@ConditionalOnExpression("!'${niblet}'.isEmpty()")
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "niblet")
public class WebSecurityConfigProperties {

	private List<String> excludePath = new ArrayList<>(50);
	
	private String sysadmin;
	
}
