package com.yuge.ing.sentinel.portal.interceptor;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.ResourceTypeConstants;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.adapter.spring.webmvc.SentinelWebInterceptor;
import com.alibaba.csp.sentinel.adapter.spring.webmvc.config.SentinelWebMvcConfig;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.yuge.ing.sentinel.portal.utils.IpUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 对IP进行埋点，默认第一个参数； （需考虑预留埋点参数）
 * 可在热点参数规则，进行配置；
 *
 * 实现功能：
 * 单服务IP限流；
 * 单接口IP限流；
 *
 * 以上仅针对单体Spring Boot应用，Spring Cloud场景下网关流控规则本身支持IP限流，无需改造
 *
 * @author: yuge
 * @date: 2024/9/13
 **/
public class EnhanceSentinelInterceptor extends SentinelWebInterceptor {

    private final SentinelWebMvcConfig config;

    public EnhanceSentinelInterceptor() {
        this(new SentinelWebMvcConfig());
    }

    public EnhanceSentinelInterceptor(SentinelWebMvcConfig config) {
        super(config);
        if (config == null) {
            // Use the default config by default.
            this.config = new SentinelWebMvcConfig();
        } else {
            this.config = config;
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return handleIpRateLimiting(request, response) && handleResourceRateLimiting(request, response);
    }

    /**
     * handle ip rate limiting
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    private boolean handleIpRateLimiting(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String resourceName = request.getContextPath();

        if (StringUtil.isEmpty(resourceName)) {
            return true;
        }

        return entry(resourceName, request, response);
    }

    /**
     * handle resource rate limiting
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    private boolean handleResourceRateLimiting(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String resourceName = getResourceName(request);

        if (StringUtil.isEmpty(resourceName)) {
            return true;
        }

        if (increaseReferece(request, config.getRequestRefName(), 1) != 1) {
            return true;
        }

        return entry(resourceName, request, response);
    }

    /**
     * entry
     *
     * @param resourceName
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    private boolean entry(String resourceName, HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            // Parse the request origin using registered origin parser.
            String origin = parseOrigin(request);
            String contextName = getContextName(request);
            String ipAddr = IpUtils.getIpAddr(request);
            ContextUtil.enter(contextName, origin);
            Object[] args = {ipAddr};
            Entry entry = SphU.entry(resourceName, ResourceTypeConstants.COMMON_WEB, EntryType.IN, args);
            request.setAttribute(config.getRequestAttributeName(), entry);
            return true;
        } catch (BlockException e) {
            try {
                handleBlockException(request, response, e);
            } finally {
                ContextUtil.exit();
            }
            return false;
        }
    }

    /**
     * @param request
     * @param rcKey
     * @param step
     * @return reference count after increasing (initial value as zero to be increased)
     */
    private Integer increaseReferece(HttpServletRequest request, String rcKey, int step) {
        Object obj = request.getAttribute(rcKey);

        if (obj == null) {
            // initial
            obj = Integer.valueOf(0);
        }

        Integer newRc = (Integer)obj + step;
        request.setAttribute(rcKey, newRc);
        return newRc;
    }

}
