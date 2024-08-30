package com.yuge.ing.sentinel.gateway.server.exception;

import com.alibaba.nacos.common.utils.NumberUtils;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.WebProperties.Resources;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * 参考： https://cloud.tencent.com/developer/article/1357313
 *
 * @author: yuge
 * @date: 2024/8/22
 **/
public class JsonErrorExceptionHandler extends DefaultErrorWebExceptionHandler {


    private static final int ERROR_CODE_LIMIT = 10000;

    /**
     * Create a new {@code DefaultErrorWebExceptionHandler} instance.
     *
     * @param errorAttributes    the error attributes
     * @param resources          the resources configuration properties
     * @param errorProperties    the error configuration properties
     * @param applicationContext the current application context
     * @since 2.4.0
     */
    public JsonErrorExceptionHandler(ErrorAttributes errorAttributes, Resources resources, ErrorProperties errorProperties, ApplicationContext applicationContext) {
        super(errorAttributes, resources, errorProperties, applicationContext);
    }

    @Override
    protected Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        int code;
        Throwable error = super.getError(request);
        code = HttpStatus.INTERNAL_SERVER_ERROR.value();
        if (error instanceof IngException) {
            IngException ve = (IngException) error;
            return response(NumberUtils.toInt(ve.getErrorCode(), code), this.buildMessage(request, error));
        }
        return response(code, this.buildMessage(request, error));
    }

    /**
     * 构建异常信息
     *
     * @param request
     * @param ex
     * @return
     */
    private String buildMessage(ServerRequest request, Throwable ex) {
        StringBuilder message = new StringBuilder("Failed to handle request [");
        message.append(request.methodName());
        message.append(" ");
        message.append(request.uri());
        message.append("]");
        if (ex != null) {
            message.append(": ");
            message.append(ex.getMessage());
        }
        return message.toString();
    }

    /**
     * Create a RouterFunction that can route and handle errors as JSON responses
     *
     * @param errorAttributes the {@code ErrorAttributes} instance to use to extract error
     * information
     * @return
     */
    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    public static Map<String, Object> response(int code, String msg) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        map.put("msg", msg);
        map.put("data", null);
        return map;
    }

    @Override
    protected int getHttpStatus(Map<String, Object> errorAttributes) {
        Integer code = (Integer) errorAttributes.get("code");
        if (isErrorCode(code)) {
            return HttpStatus.INTERNAL_SERVER_ERROR.value();
        }
        return code;
    }

    private boolean isErrorCode(Integer code) {
        if (code == null) {
            return false;
        }
        return code.intValue() >= ERROR_CODE_LIMIT;
    }

}
