package org.weicong.common.page;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * @description
 * @author weicong
 * @date 2020年1月14日
 * @version 1.0
 */
@Deprecated
@Slf4j
@Aspect
@Component
public class PageAspect {
	
	@Autowired
	private ObjectMapper objectMapper;

	@Pointcut("@annotation(pageable)")
	public void annotationPointcut(Pageable pageable) {}
	
	
	@Around("annotationPointcut(pageable)")
    public Object around(ProceedingJoinPoint pjp, Pageable pageable) throws Throwable {
		
		//获取请求方法
        Signature sig = pjp.getSignature();
        String method = pjp.getTarget().getClass().getName() + "." + sig.getName();

        //获取请求的参数
        Object[] args = pjp.getArgs();
        //fastjson转换
//        String params = JSONObject.toJSONString(args);
        String params = objectMapper.writeValueAsString(args);

        //打印请求参数
        log.info("参数:" + method + ":" + params);

        //执行方法
        Object result = pjp.proceed();
//        PageVO pageVO = new PageVO();
//        pageVO.setSize(Long.valueOf(String.valueOf(args[0])));
//        pageVO.setCurrent(Long.valueOf(String.valueOf(args[1])));
//        pageVO.setSortRule(String.valueOf(args[2]));
//        Page<?> page = PageUtil.toMybatisPage(pageVO);
//        pjp.

//        PageUtil.toPage(page);
        
        //打印返回结果
        log.info("返回结果:" + method + ":" + result);
        return result;
    }
}
