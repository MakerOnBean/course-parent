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


    /**
     * 定时任务 用于导入
     */
    @Scheduled(cron = "0 * * * * ? ")
    public void taskImportToRedis() {
        rabbitService.send(RabbitConst.EXCHANGE_DIRECT_TASK_IMPORT,RabbitConst.ROUTING_TASK_IMPORT,"");
    }


    /**
     * 定时任务 用于清理数据
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void clear() {
        rabbitService.send(RabbitConst.EXCHANGE_DIRECT_TASK_CLEAR,RabbitConst.ROUTING_TASK_CLEAR,"");
    }
}
