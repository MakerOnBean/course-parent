package cloud.makeronbean.course.common.handler;

import cloud.makeronbean.course.common.exception.CourseException;
import cloud.makeronbean.course.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author makeronbean
 * @createTime 2022-11-23  13:27
 * @description TODO
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result error(Exception e){
        e.printStackTrace();
        return Result.fail();
    }

    /**
     * 自定义异常处理方法
     * @param e
     * @return
     */
    @ExceptionHandler(CourseException.class)
    @ResponseBody
    public Result error(CourseException e){
        return Result.fail(e.getMessage());
    }
}
