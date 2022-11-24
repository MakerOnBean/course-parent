package cloud.makeronbean.course.model.course;

import cloud.makeronbean.course.model.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author makeronbean
 * @createTime 2022-11-23  20:19
 * @description TODO
 */
@Data
@TableName("course_selectable_kind")
public class CourseSelectableKind extends BaseEntity {
    private String xkCode;
    private String xkName;
    private Date startTime;
    private Date endTime;

    @TableField(exist = false)
    transient List<CourseSelectable> courseSelectableList = new ArrayList<>();
}