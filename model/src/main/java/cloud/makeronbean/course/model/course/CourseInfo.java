package cloud.makeronbean.course.model.course;

import cloud.makeronbean.course.model.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author makeronbean
 * @createTime 2022-11-23  15:48
 * @description TODO
 */
@Data
@TableName("course_info")
public class CourseInfo extends BaseEntity {
    private String name;
    private String teacherName;
    private String startTime;
    private Integer type;
}
