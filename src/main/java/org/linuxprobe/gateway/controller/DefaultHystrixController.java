package org.linuxprobe.gateway.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * 默认降级处理
 */
@Controller
public class DefaultHystrixController {
    @ResponseBody
    @RequestMapping("/defaultfallback")
    public Mono<Map<String, Object>> defaultfallback() {
        Map<String, Object> result = new HashMap<>();
        result.put("message", "For some reason, there is an error inside the server, the fuse mechanism is triggered, please try again later");
        result.put("errorcode", 500);
        return Mono.just(result);
    }
}
