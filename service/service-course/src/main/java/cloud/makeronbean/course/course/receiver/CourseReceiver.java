package cloud.makeronbean.course.course.receiver;

import cloud.makeronbean.course.course.service.CourseService;
import cloud.makeronbean.course.model.activity.XkRecode;
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

/**
 * @author makeronbean
 * @createTime 2022-11-24  16:08
 * @description TODO
 */
@Component
@Slf4j
public class CourseReceiver {

    @Autowired
    private CourseService courseService;

    /**
     * 监听确定选课队列
     */
    @SneakyThrows
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = RabbitConst.QUEUE_XK,durable = "true",autoDelete = "false"),
            exchange = @Exchange(value = RabbitConst.EXCHANGE_DIRECT_XK,durable = "true",autoDelete = "false"),
            key = RabbitConst.ROUTING_XK
    ))
    public void handleXk(XkRecode xkRecode, Message message, Channel channel) {
        try {
            log.info("xkRecode------------->{}",xkRecode);
            courseService.handleXk(xkRecode);
        } finally {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }
    }
}
