package cloud.makeronbean.course.course;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author makeronbean
 * @createTime 2022-11-23  15:42
 * @description TODO
 */
@SpringBootApplication
@ComponentScan("cloud.makeronbean.course")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "cloud.makeronbean.course.client")
public class ServiceCourseApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceCourseApplication.class, args);
    }
}
