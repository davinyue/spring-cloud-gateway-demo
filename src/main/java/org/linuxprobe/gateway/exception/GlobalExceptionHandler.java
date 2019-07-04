package org.linuxprobe.gateway.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class GlobalExceptionHandler extends DefaultErrorWebExceptionHandler {
    public GlobalExceptionHandler(ErrorAttributes errorAttributes, ResourceProperties resourceProperties, ErrorProperties errorProperties, ApplicationContext applicationContext) {
        super(errorAttributes, resourceProperties, errorProperties, applicationContext);
    }

    /**
     * 获取异常属性
     */
    @Override
    protected Map<String, Object> getErrorAttributes(ServerRequest request, boolean includeStackTrace) {
        int code = 500;
        Throwable error = super.getError(request);
        GlobalExceptionHandler.log.error("", error);
        if (error instanceof org.springframework.cloud.gateway.support.NotFoundException) {
            code = 404;
        }
        String message = error.getMessage();
        if (message == null || message.isEmpty()) {
            message = "服务器发生错误:" + error.getClass().getSimpleName();
        }
        return GlobalExceptionHandler.response(code, message);
    }

    /**
     * 根据code获取对应的HttpStatus
     */
    @Override
    protected HttpStatus getHttpStatus(Map<String, Object> errorAttributes) {
        return HttpStatus.OK;
    }

    /**
     * 构建返回的JSON数据格式
     *
     * @param errorcode 错误码
     * @param msg       信息
     * @return
     */
    public static Map<String, Object> response(int errorcode, String msg) {
        Map<String, Object> map = new HashMap<>();
        map.put("errorcode", errorcode);
        map.put("msg", msg);
        return map;
    }
}
