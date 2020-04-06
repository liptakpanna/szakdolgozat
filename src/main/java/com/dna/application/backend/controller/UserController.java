package com.dna.application.backend.controller;

import com.dna.application.backend.dto.UserDto;
import com.dna.application.backend.exception.EntityNameAlreadyExistsException;
import com.dna.application.backend.model.User;
import com.dna.application.backend.model.UserRequest;
import com.dna.application.backend.model.UsernameListResponse;
import com.dna.application.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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


    @GetMapping("/list")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseBody
    public List<UserDto> getUsers( ){
        return userService.getUsers();
    }

    @GetMapping("/usernamelist")
    @PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_RESEARCHER')")
    @ResponseBody
    public UsernameListResponse getUsernames(Authentication authentication ){
        User user = (User)authentication.getPrincipal();
        return userService.getUsernames(user.getUsername());
    }

    @PostMapping("/delete")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseBody
    public ResponseEntity<Boolean> deleteUser(@RequestParam Long id, Authentication authentication) throws Exception {
        User user = (User)authentication.getPrincipal();
        if (userService.deleteUser(id, user))
            return ResponseEntity.ok(true);
        else throw new Exception("Delete was not successful");
    }

    @PostMapping("/me/update")
    @ResponseBody
    public ResponseEntity<Boolean> updateOwnData(@RequestBody UserRequest userRequest, Authentication authentication) throws Exception {
        User user = (User)authentication.getPrincipal();
        userRequest.setId(user.getId());
        if (userRequest.getRole() != null) throw new Exception("You cannot change your role");
        if (userRequest.getStatus() != null) throw new Exception("You cannot change your status");
        try{
            return ResponseEntity.ok(userService.updateUser(userRequest, user.getUsername()));
        } catch(EntityNameAlreadyExistsException e) {
            throw new Exception("Username already exists",e);
        }
    }

    @PostMapping("/update")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseBody
    public ResponseEntity<Boolean> updateData(@RequestBody UserRequest userRequest, Authentication authentication) throws Exception {
        User user = (User)authentication.getPrincipal();
        if (userRequest.getRole() != null && user.getId().equals(userRequest.getId())) throw new Exception("You cannot change your role");
        try{
            return ResponseEntity.ok(userService.updateUser(userRequest, user.getUsername()));
        } catch(EntityNameAlreadyExistsException e) {
            throw new Exception("Username already exists",e);
        }
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
    public ResponseEntity<Boolean> addUser(@RequestBody UserRequest userRequest, Authentication authentication) throws Exception{
        User user = (User)authentication.getPrincipal();
        try{
            return ResponseEntity.ok(userService.addUser(userRequest, user.getUsername()));
        } catch(EntityNameAlreadyExistsException e) {
            throw new Exception("Username already exists",e);
        }
    }
}
