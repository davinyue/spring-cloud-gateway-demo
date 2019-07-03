package org.linuxprobe.gateway.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Setter
@Getter
@Configuration
@ConfigurationProperties("system")
public class SystemProperties {
    /**
     * 重定向配置
     */
    private Map<String, String> redirections;
}
