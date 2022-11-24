package cloud.makeronbean.course.model.student;

import cloud.makeronbean.course.model.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author makeronbean
 * @createTime 2022-11-23  14:45
 * @description TODO
 */
@Data
@TableName("student_info")
public class StudentInfo extends BaseEntity {
    private String studentCode;
    private String name;
    private Integer age;
    private Integer sex;
    private Long classId;

    @TableField(exist = false)
    private String className;
}
