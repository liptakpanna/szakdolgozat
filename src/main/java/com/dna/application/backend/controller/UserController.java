package com.dna.application.backend.controller;

import com.dna.application.backend.dto.UserDto;
import com.dna.application.backend.exception.EntityNameAlreadyExistsException;
import com.dna.application.backend.model.User;
import com.dna.application.backend.model.UserRequest;
import com.dna.application.backend.model.UsernameListResponse;
import com.dna.application.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @Value("${default.error.message}")
    private String errorMessage;

    @GetMapping("/list")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<UserDto> getUsers() throws Exception{
        try {
            return userService.getUsers();
        } catch(Exception e) {
            throw new Exception(errorMessage);
        }
    }

    @GetMapping("/usernamelist")
    @PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_RESEARCHER')")
    public UsernameListResponse getUsernames(Authentication authentication ) throws Exception{
        User user = (User)authentication.getPrincipal();
        try {
            return userService.getUsernames(user.getUsername());
        } catch(Exception e) {
            throw new Exception(errorMessage);
        }
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Boolean> deleteUser(@PathVariable Long id, Authentication authentication) throws Exception {
        User user = (User)authentication.getPrincipal();
        if (userService.deleteUser(id, user))
            return ResponseEntity.ok(true);
        else throw new Exception("Delete was not successful");
    }

    @PutMapping("/me/update")
    public ResponseEntity<Boolean> updateOwnData(@RequestBody UserRequest userRequest, Authentication authentication) throws Exception {
        User user = (User)authentication.getPrincipal();
        userRequest.setId(user.getId());
        if (userRequest.getRole() != null) throw new Exception("You cannot change your role");
        if (userRequest.getStatus() != null) throw new Exception("You cannot change your status");
        try{
            return ResponseEntity.ok(userService.updateUser(userRequest, user.getUsername()));
        } catch(EntityNameAlreadyExistsException e) {
            throw new Exception("Username already exists");
        } catch (Exception e) {
            throw new Exception(errorMessage);
        }
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Boolean> updateData(@RequestBody UserRequest userRequest, Authentication authentication)
            throws Exception {
        User user = (User)authentication.getPrincipal();
        if (userRequest.getRole() != null && user.getId().equals(userRequest.getId()))
            throw new Exception("You cannot change your role");
        try {
            return ResponseEntity.ok(userService.updateUser(userRequest, user.getUsername()));
        } catch(EntityNameAlreadyExistsException e) {
            throw new Exception("Username already exists");
        } catch (Exception e) {
            throw new Exception(errorMessage);
        }
    }

    @GetMapping("/me")
    public UserDto getUser(Authentication authentication) throws Exception {
        User user = (User)authentication.getPrincipal();
        try {
            return userService.getUserDto(user.getUsername());
        } catch (Exception e) {
            throw new Exception(errorMessage);
        }
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Boolean> addUser(@RequestBody UserRequest userRequest, Authentication authentication) throws Exception{
        User user = (User)authentication.getPrincipal();
        try{
            return ResponseEntity.ok(userService.addUser(userRequest, user.getUsername()));
        } catch(EntityNameAlreadyExistsException e) {
            throw new Exception("Username already exists");
        } catch (Exception e) {
            throw new Exception(errorMessage);
        }
    }
}
