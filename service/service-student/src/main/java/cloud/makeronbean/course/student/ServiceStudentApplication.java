package cloud.makeronbean.course.student;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author makeronbean
 * @createTime 2022-11-23  14:15
 * @description TODO
 */
@SpringBootApplication
@ComponentScan("cloud.makeronbean.course")
@EnableDiscoveryClient
@EnableFeignClients

public class ServiceStudentApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceStudentApplication.class, args);
    }
}
