package cloud.makeronbean.course.client.impl;

import cloud.makeronbean.course.client.StudentFeignClient;
import org.springframework.stereotype.Component;

/**
 * @author makeronbean
 * @createTime 2022-11-23  15:40
 * @description TODO
 */
@Component
public class StudentFallbackImpl implements StudentFeignClient {
    @Override
    public Long getClassId(Long studentId) {
        return null;
    }
}
