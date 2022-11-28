package cloud.makeronbean.course.model.course;

import cloud.makeronbean.course.model.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author makeronbean
 * @createTime 2022-11-23  20:20
 * @description TODO
 */
@Data
@TableName("course_selectable")
public class CourseSelectable extends BaseEntity {
    private Long kindId;
    private Long courseId;
    private String courseName;
    private String teacherName;
    private Long count;

    /**
     * 判断是否可以选择
     * 可以选择：1
     * 已选择：2
     * 不可选：3
     */
    @TableField(exist = false)
    private String selectFlag;

    @TableField(exist = false)
    private String selectFlagName;
}
