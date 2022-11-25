package cloud.makeronbean.course.activity.controller;

import cloud.makeronbean.course.activity.block.XkBlockHandler;
import cloud.makeronbean.course.activity.service.ActivityService;
import cloud.makeronbean.course.common.limit.Limit;
import cloud.makeronbean.course.common.result.Result;
import cloud.makeronbean.course.common.util.AuthContextHolder;
import cloud.makeronbean.course.model.course.CourseSelectable;
import cloud.makeronbean.course.model.course.CourseSelectableKind;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author makeronbean
 * @createTime 2022-11-23  23:14
 * @description TODO
 */
@RestController
@RequestMapping("/activity")
public class ActivityController {
    @Autowired
    private ActivityService activityService;


    /**
     * 根据xkCode获取对应选课信息
     * /activity/getKindList
     */
    @GetMapping("/getKindList")
    @SentinelResource(value = "getKindList",
            blockHandlerClass = {XkBlockHandler.class},
            blockHandler = "handlerKindList")
    public Result getKindList(HttpServletRequest request) {
        String xkCode = AuthContextHolder.getXkCode(request);
        List<CourseSelectableKind> courseSelectableKindList = activityService.getKindList(xkCode);
        return Result.ok(courseSelectableKindList);
    }

    /**
     * 获取具体可选课程列表
     * /activity/getDetailList/{kindId}
     */
    @GetMapping("/getDetailList/{kindId}")
    @Limit
    @SentinelResource(value = "getDetailList",
            blockHandlerClass = XkBlockHandler.class,
            blockHandler = "handlerDetailList")
    public Result getDetailList(HttpServletRequest request,
                                @PathVariable Long kindId) {
        String studentId = AuthContextHolder.getStudentId(request);
        String xkCode = AuthContextHolder.getXkCode(request);
        List<CourseSelectable> courseSelectableList = activityService.getDetailList(xkCode, kindId, studentId);
        return Result.ok(courseSelectableList);
    }

    /**
     * 选择具体的课程
     * /activity/select/{courseSelectableId}
     */
    @PostMapping("/select/{kindId}/{courseSelectableId}")
    @SentinelResource(value = "select",
            blockHandlerClass = XkBlockHandler.class,
            blockHandler = "handlerSelect")
    public Result select(HttpServletRequest request,
                         @PathVariable Long kindId,
                         @PathVariable Long courseSelectableId) {
        String studentId = AuthContextHolder.getStudentId(request);
        String xkCode = AuthContextHolder.getXkCode(request);

        return activityService.select(courseSelectableId, Long.valueOf(studentId), xkCode,kindId);
    }

    /**
     * 查询选课是否成功
     * /activity/isSuccess/{courseSelectableId}
     */
    @GetMapping("/isSuccess/{courseSelectableId}")
    @SentinelResource(value = "isSuccess",blockHandlerClass = XkBlockHandler.class,blockHandler = "handlerIsSuccess")
    public Result isSuccess(HttpServletRequest request,
                            @PathVariable Long courseSelectableId) {
        String xkCode = AuthContextHolder.getXkCode(request);
        String studentId = AuthContextHolder.getStudentId(request);
        return this.activityService.isSuccess(courseSelectableId,xkCode,Long.valueOf(studentId));
    }

}
