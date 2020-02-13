package com.dna.application.backend.controller;

import com.dna.application.backend.model.User;
import com.dna.application.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/v1")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    /*@PostMapping("/login")
    @ResponseBody
    public String login(@RequestParam String username, @RequestParam String pwd){
       User user = userRepository.findByUsername(username);
       if (user == null) return "Username does not exist...";
       else if (user.getPassword().equals(pwd)) return "Login successfull!";
       else return "Wrong password...";
    }*/
}
