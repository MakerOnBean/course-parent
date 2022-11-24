package cloud.makeronbean.course.client;

import cloud.makeronbean.course.client.impl.StudentFallbackImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author makeronbean
 * @createTime 2022-11-23  15:39
 * @description TODO
 */
@Component
@FeignClient(value = "service-student",fallback = StudentFallbackImpl.class)
public interface StudentFeignClient {
    /**
     * 通过studentId获取classId
     * /api/student/getClassId/{studentId}
     */
    @GetMapping("/api/student/getClassId/{studentId}")
    Long getClassId(@PathVariable Long studentId);
}
