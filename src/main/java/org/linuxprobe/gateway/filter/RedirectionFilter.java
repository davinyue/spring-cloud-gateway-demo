package org.linuxprobe.gateway.filter;


import org.linuxprobe.gateway.configuration.SystemProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

/**
 * 重定向拦截器, 实现自定义重定向
 */
public class RedirectionFilter implements GlobalFilter, Ordered {
    private SystemProperties systemProperties;

    public RedirectionFilter(SystemProperties systemProperties) {
        this.systemProperties = systemProperties;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Map<String, String> redirections = this.systemProperties.getRedirections();
        if (redirections != null && !redirections.isEmpty()) {
            URI uri = exchange.getRequest().getURI();
            // 如果需要重定向
            if (redirections.containsKey(uri.getPath())) {
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.FOUND);
                String redirectionPath = redirections.get(uri.getPath());
                if (uri.getQuery() != null) {
                    redirectionPath += "?" + uri.getQuery();
                }
                response.getHeaders().put(HttpHeaders.LOCATION, Collections.singletonList(redirectionPath));
                return response.setComplete();
            }
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -3;
    }
}
