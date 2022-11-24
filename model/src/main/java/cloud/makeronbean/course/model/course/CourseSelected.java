package cloud.makeronbean.course.model.course;

import cloud.makeronbean.course.model.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author makeronbean
 * @createTime 2022-11-24  17:26
 * @description TODO
 */
@Data
@TableName("course_selected")
public class CourseSelected extends BaseEntity {
    private Long studentId;
    private Long courseId;
}
