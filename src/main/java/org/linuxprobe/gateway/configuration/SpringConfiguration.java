package org.linuxprobe.gateway.configuration;

import lombok.Getter;
import lombok.Setter;
import org.linuxprobe.gateway.filter.RedirectionFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
public class SpringConfiguration {
    @Bean
    public RedirectionFilter edirectionFilter() {
        return new RedirectionFilter();
    }
}
