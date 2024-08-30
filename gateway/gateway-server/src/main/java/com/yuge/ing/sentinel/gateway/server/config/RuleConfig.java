package com.yuge.ing.sentinel.gateway.server.config;

import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayParamFlowItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import com.alibaba.nacos.shaded.com.google.common.collect.Sets;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

import static com.alibaba.csp.sentinel.adapter.gateway.common.SentinelGatewayConstants.PARAM_PARSE_STRATEGY_CLIENT_IP;

/**
 * @author: yuge
 * @date: 2024/8/22
 **/
@Configuration
public class RuleConfig {

    @PostConstruct
    public void init() {
        GatewayFlowRule gatewayFlowRule = new GatewayFlowRule("portal-server");
        gatewayFlowRule.setCount(1);
        gatewayFlowRule.setIntervalSec(60);
        gatewayFlowRule.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_DEFAULT);
        GatewayParamFlowItem gatewayParamFlowItem = new GatewayParamFlowItem();
        gatewayParamFlowItem.setParseStrategy(PARAM_PARSE_STRATEGY_CLIENT_IP);
        gatewayFlowRule.setParamItem(gatewayParamFlowItem);
        GatewayRuleManager.loadRules(Sets.newHashSet(gatewayFlowRule));
    }



}
