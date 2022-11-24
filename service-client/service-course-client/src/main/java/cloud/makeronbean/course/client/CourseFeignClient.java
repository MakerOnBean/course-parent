package cloud.makeronbean.course.client;

import cloud.makeronbean.course.client.impl.CourseFallbackImpl;
import cloud.makeronbean.course.model.course.CourseSelectable;
import cloud.makeronbean.course.model.course.CourseSelectableKind;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

/**
 * @author makeronbean
 * @createTime 2022-11-23  20:52
 * @description TODO
 */
@Component
@FeignClient(value = "service-course")
public interface CourseFeignClient {

    /**
     * 查询当日所有可选课信息
     * /api/course/getAllSelectable
     */
    @GetMapping("/api/course/getAllSelectable")
    List<CourseSelectableKind> getAllSelectable();
}