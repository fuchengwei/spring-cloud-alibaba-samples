package com.ichinae.samples.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fuchengwei
 * @date 2020/12/30 2:45 下午
 */
@Data
@Component
@ConfigurationProperties(prefix = "filter.login")
public class FilterConfig {
    private List<String> ignoreUrl = new ArrayList<>();
}
