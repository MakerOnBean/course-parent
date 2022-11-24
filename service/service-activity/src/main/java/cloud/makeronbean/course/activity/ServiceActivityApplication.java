package cloud.makeronbean.course.activity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author makeronbean
 * @createTime 2022-11-23  19:40
 * @description TODO
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@ComponentScan("cloud.makeronbean.course")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "cloud.makeronbean.course.client")
public class ServiceActivityApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceActivityApplication.class, args);
    }
}
