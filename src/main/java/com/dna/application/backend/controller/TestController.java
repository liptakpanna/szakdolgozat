package com.dna.application.backend.controller;

import com.dna.application.backend.dto.UserDto;
import com.dna.application.backend.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TestController {
    @Autowired
    private TestService testService;

    @GetMapping("/test")
    public List<UserDto> setTestData(){
        return testService.setTestData();
    }

}
