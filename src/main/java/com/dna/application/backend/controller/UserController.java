package com.dna.application.backend.controller;

import com.dna.application.backend.dto.UserDto;
import com.dna.application.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    /*@PostMapping("/login")
    @ResponseBody
    public String login(@RequestParam String username, @RequestParam String pwd){
       User user = userRepository.findByUsername(username);
       if (user == null) return "Username does not exist...";
       else if (user.getPassword().equals(pwd)) return "Login successfull!";
       else return "Wrong password...";
    }*/

    @GetMapping("/list")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseBody
    public List<UserDto> getUsers( ){
        return userService.getUsers();
    }
}
