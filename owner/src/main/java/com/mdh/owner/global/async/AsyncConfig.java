package com.mdh.owner.global.async;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {


    /*
    비동기 작업 스레드 풀 설정 우선 기본값 사용 자세한 내용은 ThreadPoolTaskExecutor 내에 있음.
    따로 설정하지 않으면 ThreadPoolTaskExecutor 내의 값이 default 값으로 사용됨
    * */
    /*@Override
    public Executor getAsyncExecutor() {
        var executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(10000);
        executor.setKeepAliveSeconds(3);
        executor.setThreadNamePrefix("default-async");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }*/
}