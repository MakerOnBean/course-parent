package cloud.makeronbean.course.activity.receiver;

import cloud.makeronbean.course.activity.service.ActivityService;
import cloud.makeronbean.course.client.CourseFeignClient;
import cloud.makeronbean.course.model.activity.XkRecode;
import cloud.makeronbean.course.model.course.CourseSelectable;
import cloud.makeronbean.course.model.course.CourseSelectableKind;
import cloud.makeronbean.course.rabbit.constant.RabbitConst;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author makeronbean
 * @createTime 2022-11-23  19:58
 * @description TODO
 */
@Component
@Slf4j
public class XkReceiver {

    @Autowired
    private CourseFeignClient courseFeignClient;

    @Autowired
    private ActivityService activityService;

    /**
     * 导入可选课的数据
     */
    @SneakyThrows
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = RabbitConst.QUEUE_TASK_IMPORT,durable = "true",autoDelete = "false"),
            exchange = @Exchange(value = RabbitConst.EXCHANGE_DIRECT_TASK_IMPORT,durable = "true",autoDelete = "false"),
            key = RabbitConst.ROUTING_TASK_IMPORT
    ))
    public void importToRedis(Message message, Channel channel) {
        try {
            List<CourseSelectableKind> courseSelectableKindList = courseFeignClient.getAllSelectable();
            // 清除过期缓存
            //activityService.removeCache();
            // 缓存当日可选课程
            activityService.importToRedis(courseSelectableKindList);
            log.info("courseSelectableKindList------->{}",courseSelectableKindList);
        } finally {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }
    }


    /**
     * 清理过期数据
     */
    @SneakyThrows
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = RabbitConst.QUEUE_TASK_CLEAR,durable = "true",autoDelete = "false"),
            exchange = @Exchange(value = RabbitConst.EXCHANGE_DIRECT_TASK_CLEAR,durable = "true",autoDelete = "false"),
            key = RabbitConst.ROUTING_TASK_CLEAR
    ))
    public void clearRedis(Message message, Channel channel) {
        try {
            activityService.removeCache();
        } finally {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }
    }

    /**
     * 数据库处理失败 数据回滚
     */
    @SneakyThrows
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = RabbitConst.QUEUE_ROLLBACK,durable = "true",autoDelete = "false"),
            exchange = @Exchange(value = RabbitConst.EXCHANGE_DIRECT_ROLLBACK,durable = "true",autoDelete = "false"),
            key = RabbitConst.ROUTING_ROLLBACK
    ))
    public void rollback(XkRecode xkRecode, Message message, Channel channel) {
        try {
            log.info("xkRecode------------------->{}",xkRecode);
            this.activityService.rollback(xkRecode);
        } finally {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }
    }
}