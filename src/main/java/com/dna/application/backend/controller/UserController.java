package com.dna.application.backend.controller;

import com.dna.application.backend.dto.UserDto;
import com.dna.application.backend.model.User;
import com.dna.application.backend.model.UserRequest;
import com.dna.application.backend.model.UsernameListResponse;
import com.dna.application.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@CrossOrigin
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

    @GetMapping("/usernamelist")
    @PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_RESEARCHER')")
    @ResponseBody
    public UsernameListResponse getUsernames( ){
        return userService.getUsernames();
    }

    @PostMapping("/delete")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseBody
    public List<UserDto> deleteUser(@RequestParam Long id, Authentication authentication) throws Exception{
        User user = (User)authentication.getPrincipal();
        if (!user.getId().equals(id)) return userService.deleteUser(id);
        else throw new Exception("You cannot delete yourself.");
    }

    @PostMapping("/me/update")
    @ResponseBody
    public UserDto updateOwnData(@RequestBody UserRequest userRequest, Authentication authentication) throws Exception {
        User user = (User)authentication.getPrincipal();
        userRequest.setId(user.getId());
        if (userRequest.getRole() != null) throw new Exception("You cannot change your role");
        if (userRequest.getStatus() != null) throw new Exception("You cannot change your status");
        return userService.updateUser(userRequest, user.getUsername());
    }

    @PostMapping("/update")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseBody
    public UserDto updateData(@RequestBody UserRequest userRequest, Authentication authentication) throws Exception {
        User user = (User)authentication.getPrincipal();
        return userService.updateUser(userRequest, user.getUsername());
    }

    @GetMapping("/me")
    @ResponseBody
    public UserDto getUser(@RequestParam String username, Authentication authentication) throws Exception {
        User user = (User)authentication.getPrincipal();
        if (user.getUsername().equals(username)) return userService.getUserDto(username);
        else throw new Exception("You only can get your information.");
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseBody
    public List<UserDto> addUser(@RequestBody UserRequest userRequest, Authentication authentication) throws Exception{
        User user = (User)authentication.getPrincipal();
        return  userService.addUser(userRequest, user.getUsername());
    }
}
