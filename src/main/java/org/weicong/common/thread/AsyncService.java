package org.weicong.common.thread;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @description 
 * @author weicong
 * @date 2020年1月14日
 * @version 1.0
 */
@Service
public class AsyncService {

    @Async("asyncExecutor")
    public void execute(AsyncTask task){
    	task.execute();
    }
}