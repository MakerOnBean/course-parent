package cloud.makeronbean.course.course.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

/**
 * @author makeronbean
 * @createTime 2022-11-24  17:21
 * @description TODO
 */
@Configuration
public class ExecutorConfig {
    @Bean
    public ExecutorService executorService() {
        return new ThreadPoolExecutor(
                10,
                20,
                5,
                TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(40),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
    }
}
