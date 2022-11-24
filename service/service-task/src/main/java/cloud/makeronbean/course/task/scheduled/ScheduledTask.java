package cloud.makeronbean.course.task.scheduled;

import cloud.makeronbean.course.rabbit.constant.RabbitConst;
import cloud.makeronbean.course.rabbit.servcie.RabbitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author makeronbean
 * @createTime 2022-11-23  19:55
 * @description TODO
 */
@Component
@Slf4j
@EnableScheduling
public class ScheduledTask {

    @Autowired
    private RabbitService rabbitService;

    @Scheduled(cron = "0/15 * * * * ?")
    public void task18() {
        //log.info("task----->{}","success");
        rabbitService.send(RabbitConst.EXCHANGE_DIRECT_TASK,RabbitConst.ROUTING_TASK_1,"");
    }


}
