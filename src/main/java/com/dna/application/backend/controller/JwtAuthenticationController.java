package com.dna.application.backend.controller;

import com.dna.application.backend.model.JwtRequest;
import com.dna.application.backend.model.JwtResponse;
import com.dna.application.backend.model.JwtValidResponse;
import com.dna.application.backend.model.User;
import com.dna.application.backend.service.UserDetailsServiceImpl;
import com.dna.application.backend.util.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final String token = jwtTokenUtil.generateToken(userDetails);
        User user = (User)userDetails;
        return ResponseEntity.ok(new JwtResponse(token,user.getId(), user.getRole()));
    }
    
    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }

    @GetMapping("/validate")
    @ResponseBody
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
}