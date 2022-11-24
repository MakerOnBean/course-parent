package cloud.makeronbean.course.course.service.impl;

import cloud.makeronbean.course.client.StudentFeignClient;
import cloud.makeronbean.course.common.constant.RedisConst;
import cloud.makeronbean.course.common.util.DateUtil;
import cloud.makeronbean.course.course.mapper.CourseInfoMapper;
import cloud.makeronbean.course.course.mapper.CourseSelectableKindMapper;
import cloud.makeronbean.course.course.mapper.CourseSelectableMapper;
import cloud.makeronbean.course.course.mapper.CourseSelectedMapper;
import cloud.makeronbean.course.course.service.CourseService;
import cloud.makeronbean.course.model.activity.XkRecode;
import cloud.makeronbean.course.model.course.CourseInfo;
import cloud.makeronbean.course.model.course.CourseSelectable;
import cloud.makeronbean.course.model.course.CourseSelectableKind;
import cloud.makeronbean.course.model.course.CourseSelected;
import cloud.makeronbean.course.rabbit.constant.RabbitConst;
import cloud.makeronbean.course.rabbit.servcie.RabbitService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mysql.cj.log.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ExecutorService;

/**
 * @author makeronbean
 * @createTime 2022-11-23  15:47
 * @description TODO
 */
@Service
@Slf4j
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseInfoMapper courseInfoMapper;

    @Autowired
    private CourseSelectableMapper courseSelectableMapper;

    @Autowired
    private CourseSelectableKindMapper courseSelectableKindMapper;

    @Autowired
    private CourseSelectedMapper courseSelectedMapper;

    @Autowired
    private StudentFeignClient studentFeignClient;

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private RabbitService rabbitService;

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 查询学生对应课程信息
     */
    @Override
    public List<CourseInfo> list(Long studentId) {
        Long classId = studentFeignClient.getClassId(studentId);
        if (classId == null) {
            return null;
        }
        return courseInfoMapper.selectCourseInfoList(classId,studentId);
    }


    /**
     * 查询当日所有可选课信息
     */
    @Override
    public List<CourseSelectableKind> getAllSelectable() {

        QueryWrapper<CourseSelectableKind> cskw = new QueryWrapper<>();
        // 开始时间是今天
        cskw.eq("DATE_FORMAT(start_time,'%Y-%m-%d')", DateUtil.formatDate(new Date()));
        List<CourseSelectableKind> kindList = courseSelectableKindMapper.selectList(cskw);
        if (!CollectionUtils.isEmpty(kindList)) {
            // 查询类别id对应的具体选课信息
            kindList.forEach(item -> {
                LambdaQueryWrapper<CourseSelectable> csw = new LambdaQueryWrapper<>();
                csw.eq(CourseSelectable::getKindId,item.getId());
                List<CourseSelectable> courseSelectableList = this.courseSelectableMapper.selectList(csw);
                if (!CollectionUtils.isEmpty(courseSelectableList)) {
                    item.setCourseSelectableList(courseSelectableList);
                }
            });
        }
        return kindList;
    }


    /**
     * 处理选课消息
     */
    @Override
    public void handleXk(XkRecode xkRecode) {
        try {
            Long studentId = xkRecode.getStudentId();
            Long courseSelectableId = xkRecode.getCourseSelectableId();
            CourseSelectable courseSelectable = this.courseSelectableMapper.selectById(courseSelectableId);
            CourseSelected courseSelected = new CourseSelected();
            courseSelected.setCourseId(courseSelectable.getCourseId());
            courseSelected.setStudentId(studentId);
            courseSelectedMapper.insert(courseSelected);

            redisTemplate.boundHashOps(RedisConst.XK_STUDENT_PREFIX + xkRecode.getXkCode())
                    .put(xkRecode.getStudentId().toString(),"1");
        } catch (Exception e) {
            // 异常处理
            executorService.execute(() -> {
                // 发送恢复数量的消息
                rabbitService.send(RabbitConst.EXCHANGE_DIRECT_ROLLBACK,RabbitConst.ROUTING_ROLLBACK,xkRecode);
            });
            throw e;
        }





    }
}
