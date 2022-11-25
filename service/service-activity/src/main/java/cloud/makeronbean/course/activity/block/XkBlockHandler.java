package cloud.makeronbean.course.activity.block;

import cloud.makeronbean.course.common.result.Result;
import cloud.makeronbean.course.common.result.ResultCodeEnum;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;

/**
 * @author makeronbean
 * @createTime 2022-11-25  12:05
 * @description TODO
 */

public class XkBlockHandler {
    public static Result handlerKindList(HttpServletRequest request, BlockException e) {
        return Result.build(null, ResultCodeEnum.BUSY);
    }

    public static Result handlerDetailList(HttpServletRequest request,
                                           Long kindId,
                                           BlockException e) {
        return Result.build(null, ResultCodeEnum.BUSY);
    }

    public static Result handlerSelect(HttpServletRequest request,
                                       Long kindId,
                                       Long courseSelectableId,
                                       BlockException e) {
        return Result.build(null, ResultCodeEnum.BUSY);
    }

    public static Result handlerIsSuccess(HttpServletRequest request,
                                          Long courseSelectableId,
                                          BlockException e) {
        return Result.build(null, ResultCodeEnum.BUSY);
    }
}
