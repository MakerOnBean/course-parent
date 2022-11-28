package cloud.makeronbean.course.activity.receiver;

import cloud.makeronbean.course.activity.cache.StateCacheHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author makeronbean
 * @createTime 2022-11-25  21:17
 * @description TODO
 */
@Component
@Slf4j
public class MessageReceive {

    public void receiveMessage(String message) {
        log.info("MessageReceive.receiveMessage 已接收到消息------------>{}",message);
        if (message.startsWith("clear")) {
            StateCacheHelper.removeAll();
        }
        message = message.replaceAll("\"","");
        String[] split = message.split(":");
        if (split.length == 2) {
            Long key = Long.valueOf(split[0]);
            boolean state = Boolean.parseBoolean(split[1]);
            if (state) {
                StateCacheHelper.setStateTrue(key);
            } else {
                StateCacheHelper.setStateFalse(key);
            }
        }
    }

}
