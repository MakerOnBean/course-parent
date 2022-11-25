package cloud.makeronbean.course.rabbit.servcie;

import cloud.makeronbean.course.rabbit.model.CourseCorrelationData;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author makeronbean
 * @createTime 2022-11-23  18:29
 * @description TODO
 */
@Service
@Slf4j
public class RabbitService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;


    /**
     * 抽取发送消息方法
     */
    public boolean send(String exchange, String routingKey, Object message) {

        // 发送消息前，构建实体类
        CourseCorrelationData courseCorrelationData = this.buildCourseCorrelationData(exchange,routingKey,message);

        // 存储到redis
        redisTemplate.opsForValue().set(Objects.requireNonNull(courseCorrelationData.getId()), JSON.toJSONString(courseCorrelationData),1, TimeUnit.MINUTES);

        // 发送消息时将 CorrelationData 对象传入
        rabbitTemplate.convertSendAndReceive(exchange,routingKey,message,courseCorrelationData);
        log.info("消息发送成功--------->{}",message);
        return true;
    }


    /**
     * 封装 CourseCorrelationData 对象
     */
    private CourseCorrelationData buildCourseCorrelationData(String exchange, String routingKey, Object message) {
        CourseCorrelationData courseCorrelationData = new CourseCorrelationData();
        // 设置id
        String id = UUID.randomUUID().toString().replaceAll("-", "");
        courseCorrelationData.setId(id);
        // 设置消息主体
        courseCorrelationData.setMessage(message);
        // 设置交换机
        courseCorrelationData.setExchange(exchange);
        // 设置routingKey
        courseCorrelationData.setRoutingKey(routingKey);
        return courseCorrelationData;
    }

}
