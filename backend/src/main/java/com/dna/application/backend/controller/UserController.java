package com.dna.application.backend.controller;

import com.dna.application.backend.dto.UserDto;
import com.dna.application.backend.exception.EntityNameAlreadyExistsException;
import com.dna.application.backend.model.User;
import com.dna.application.backend.model.UserRequest;
import com.dna.application.backend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/list")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<UserDto> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/usernamelist")
    @PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_RESEARCHER')")
    public ResponseEntity<List<String>> getUsernames(Authentication authentication )  {
        User user = (User)authentication.getPrincipal();
        return ResponseEntity.ok(userService.getUsernames(user.getUsername()));
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateData(@RequestBody UserRequest userRequest, Authentication authentication) {
        User user = (User)authentication.getPrincipal();
        if(userRequest.getId() == null)
            userRequest.setId(user.getId());
        else if(user.getRole() != User.Role.ADMIN) {
            Map.Entry<String,String> value=new AbstractMap.SimpleEntry<>("message","No id provided");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(value);
        }
        try {
            return ResponseEntity.ok(userService.updateUser(userRequest, user));
        } catch(EntityNameAlreadyExistsException e) {
            Map.Entry<String,String> value=new AbstractMap.SimpleEntry<>("message","Username already exists");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(value);
        } catch (Exception e) {
            Map.Entry<String,String> value=new AbstractMap.SimpleEntry<>("message",e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(value);
        }
    }

    @GetMapping("/me")
    public UserDto getUser(Authentication authentication) {
        User user = (User)authentication.getPrincipal();
        return userService.getUserDto(user.getUsername());
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> addUser(@RequestBody UserRequest userRequest, Authentication authentication) {
        User user = (User)authentication.getPrincipal();
        try{
            return ResponseEntity.ok(userService.addUser(userRequest, user.getUsername()));
        } catch(EntityNameAlreadyExistsException e) {
            Map.Entry<String,String> value=new AbstractMap.SimpleEntry<>("message","Username already exists");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(value);
        } catch (Exception e) {
            Map.Entry<String,String> value=new AbstractMap.SimpleEntry<>("message",e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(value);
        }
    }
}
