package cloud.makeronbean.course.client.impl;

import cloud.makeronbean.course.client.CourseFeignClient;
import cloud.makeronbean.course.model.course.CourseSelectable;
import cloud.makeronbean.course.model.course.CourseSelectableKind;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author makeronbean
 * @createTime 2022-11-23  20:53
 * @description TODO
 */
@Component
public class CourseFallbackImpl implements CourseFeignClient {
    @Override
    public List<CourseSelectableKind> getAllSelectable() {
        return null;
    }
}