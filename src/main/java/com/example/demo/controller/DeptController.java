package com.example.demo.controller;

import com.example.demo.Dept;
import com.example.demo.service.DeptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class DeptController {

    private final L7Cache cache;
    private final DeptService deptService;

    @PostConstruct
    private void init () {
    }

    @GetMapping("/main")
    public List<Dept> main() {

        List<Dept> list = (List<Dept>) cache.get("main_key", () -> {
            log.info(">>>>>>>>>>>>>>>>>>> main_key");
           return deptService.selectList();
        });
        return list;
    }
}
