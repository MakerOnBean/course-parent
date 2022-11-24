package cloud.makeronbean.course.activity.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

/**
 * @author makeronbean
 * @createTime 2022-11-24  11:08
 * @description TODO 配置类
 */
@Configuration
public class ExecutorConfig {

    /**
     * 注入线程池
     */
    @Bean
    public ExecutorService executorService() {
        return new ThreadPoolExecutor(
                200,
                400,
                10,
                TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(500),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
    }
}
