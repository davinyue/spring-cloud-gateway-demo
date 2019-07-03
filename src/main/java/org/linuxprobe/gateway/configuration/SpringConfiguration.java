package org.linuxprobe.gateway.configuration;

import lombok.Getter;
import lombok.Setter;
import org.linuxprobe.gateway.filter.HeartbeatRequestFilter;
import org.linuxprobe.gateway.filter.MicroServiceRedirectionFilter;
import org.linuxprobe.gateway.filter.RedirectionFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
public class SpringConfiguration {
    @Bean
    public MicroServiceRedirectionFilter microServiceRedirectionFilter() {
        return new MicroServiceRedirectionFilter();
    }

    @Bean
    public HeartbeatRequestFilter heartbeatRequestFilter() {
        return new HeartbeatRequestFilter();
    }

    @Bean
    public RedirectionFilter redirectionFilter(SystemProperties systemProperties) {
        return new RedirectionFilter(systemProperties);
    }
}
