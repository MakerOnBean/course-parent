package cloud.makeronbean.course.rabbit.model;

import lombok.Data;
import org.springframework.amqp.rabbit.connection.CorrelationData;

/**
 * @author makeronbean
 * @createTime 2022-11-23  18:28
 * @description TODO
 */
@Data
public class CourseCorrelationData extends CorrelationData {
    // 消息主体
    private Object message;
    // 交换机
    private String exchange;
    // routingKey
    private String routingKey;
    // 重试次数
    private int retryCount = 0;
}
