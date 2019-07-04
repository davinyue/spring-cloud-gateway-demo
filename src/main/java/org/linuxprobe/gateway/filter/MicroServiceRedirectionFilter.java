package org.linuxprobe.gateway.filter;

import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Collections;

/**
 * 微服务重定向拦截器, 解决微服务重定向导致浏览器访问内网ip
 */
public class MicroServiceRedirectionFilter implements GlobalFilter, Ordered {

    @Override
    public int getOrder() {
        return -2;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpResponse originalResponse = exchange.getResponse();
        ServerHttpRequest request = exchange.getRequest();
        DataBufferFactory bufferFactory = originalResponse.bufferFactory();
        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                // 如果相应302重定向
                if (this.getStatusCode().value() == 302) {
                    Route requestRoute = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
                    // 如果访问的是微服务
                    if (requestRoute.getUri().toString().toLowerCase().startsWith("lb://")) {
                        URI requestUrl = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);
                        URI responseLocation = this.getHeaders().getLocation();
                        // 如果重定向host+port与请求host+port相同
                        if (requestUrl.getAuthority().equals(responseLocation.getAuthority())) {
                            // 通过请求的path减去真实请求到微服务的path, 计算出微服务访问前缀
                            String xForwardedPrefix = request.getURI().getPath();
                            xForwardedPrefix = xForwardedPrefix.substring(0, xForwardedPrefix.length() - requestUrl.getPath().length());
                            StringBuilder responseUrl = new StringBuilder(xForwardedPrefix);
                            responseUrl.append(responseLocation.getPath());
                            if (responseLocation.getQuery() != null) {
                                responseUrl.append("?");
                                responseUrl.append(responseLocation.getQuery());
                            }
                            this.getHeaders().put(HttpHeaders.LOCATION, Collections.singletonList(responseUrl.toString()));
                        }
                    }
                }
                return super.writeWith(body);
            }
        };
        return chain.filter(exchange.mutate().response(decoratedResponse).build());
    }
}
