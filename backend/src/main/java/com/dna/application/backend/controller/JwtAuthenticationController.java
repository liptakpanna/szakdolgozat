package com.dna.application.backend.controller;

import com.dna.application.backend.model.JwtRequest;
import com.dna.application.backend.model.JwtResponse;
import com.dna.application.backend.model.User;
import com.dna.application.backend.service.UserDetailsServiceImpl;
import com.dna.application.backend.service.UserService;
import com.dna.application.backend.util.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@Slf4j
@RequestMapping("/api")
public class JwtAuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private UserService userService;

    @Value("${default.error.message}")
    private String errorMessage;

    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
        try {
            authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        } catch (Exception e) {
            Map.Entry<String,String> value=new AbstractMap.SimpleEntry<>("message",e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(value);
        }
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final String token = jwtTokenUtil.generateToken(userDetails);
        User user = (User)userDetails;
        return ResponseEntity.ok(new JwtResponse(token,user.getId(), user.getRole()));
    }
    
    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED");
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS");
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateJwtToken(@RequestHeader("Authorization") String jwtToken, Authentication authentication) {
        if(authentication != null && !authentication.getName().equals("")) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(authentication.getName());
            if(userDetails != null)
                try {
                    return ResponseEntity.ok(jwtTokenUtil.validateToken(jwtToken.substring(7), userDetails));
                } catch(Exception e) {
                    return ResponseEntity.ok(false);
                }
        }
        return ResponseEntity.ok(false);
    }

    @GetMapping("/forgotpassword")
    public ResponseEntity<List<String>> getAdminEmail() {
        return ResponseEntity.ok(userService.getAdminEmail());
    }
}