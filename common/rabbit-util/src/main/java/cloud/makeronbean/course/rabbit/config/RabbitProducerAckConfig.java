package cloud.makeronbean.course.rabbit.config;

import cloud.makeronbean.course.rabbit.model.CourseCorrelationData;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * @author makeronbean
 * @createTime 2022-11-24  18:22
 * @description TODO 用于绑定 生产者 -> 交换机 与 交换机 -> 队列 失败后的回调方法
 */
@Component
@Slf4j
public class RabbitProducerAckConfig implements RabbitTemplate.ConfirmCallback,RabbitTemplate.ReturnCallback{

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;


    /**
     * bean实例化完成后执行
     * 绑定RabbitTemplate与下面两个回调方法
     */
    @PostConstruct
    public void init() {
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnCallback(this);
    }


    /**
     * 从生产者发送到交换机时触发
     * @param correlationData 数据，必须在发送消息时传递一个correlationData对象，并且消息发送失败，才会有值
     * @param ack 是否发送成功
     * @param cause 发送失败原因
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if (ack) {
            log.info("生产者 -> 交换机：消息发送成功");
        } else {
            log.info("消息发送失败：" + cause + " 数据：" + JSON.toJSONString(correlationData));
            this.retryMessage((CourseCorrelationData) correlationData);
        }
    }


    /**
     * 从交换机发送消息到队列 失败时触发（发送成功不触发）
     */
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        // 获取 CourseCorrelationData 对象的 id
        String id = (String) message.getMessageProperties().getHeaders().get("spring_returned_message_correlation");

        if (!StringUtils.isEmpty(id)) {

            // 反序列化对象输出
            log.info("交换机 -> 消息队列：消息发送失败");
            log.info("消息主体: {}",new String(message.getBody()));
            log.info("应答码: {}",replyCode);
            log.info("描述: {}",replyText);
            log.info("消息使用的交换器 exchange : {}",exchange);
            log.info("消息使用的路由键 routing : {}",routingKey);

            // 根据id从redis中查询
            String strJson = redisTemplate.opsForValue().get(id);

            CourseCorrelationData correlationData = JSON.parseObject(strJson, CourseCorrelationData.class);
            this.retryMessage(correlationData);
        }
    }

    /**
     * 重试发送消息
     * @param courseCorrelationData 自己封装的实体类，记录了交换机、路由key等，用于消息重试发送
     */
    private void retryMessage(CourseCorrelationData courseCorrelationData) {

        // 判断是否到达重试次数
        int retryCount = courseCorrelationData.getRetryCount();
        if (retryCount >= 3) {
            log.error("消息：" + courseCorrelationData.getMessage() + "------------->到达重试次数，发送失败");
            return;
        }

        // 重试次数++
        courseCorrelationData.setRetryCount(++retryCount);

        // 更新redis中的缓存
        redisTemplate.opsForValue().set(courseCorrelationData.getId(), JSON.toJSONString(courseCorrelationData),10, TimeUnit.MINUTES);

        rabbitTemplate.convertSendAndReceive(courseCorrelationData.getExchange(), courseCorrelationData.getRoutingKey(), courseCorrelationData.getMessage(), courseCorrelationData);

        log.info("重试次数------------> {}",retryCount);
    }
}
