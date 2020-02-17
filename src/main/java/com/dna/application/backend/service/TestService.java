package com.dna.application.backend.service;

import com.dna.application.backend.dto.UserDto;
import com.dna.application.backend.model.User;
import com.dna.application.backend.repository.UserRepository;
import com.dna.application.backend.security.WebSecurityConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TestService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public List<UserDto> setTestData() {
        List<User> users = new ArrayList<>();
        //users.add(new User("admin", "admin@example.com", passwordEncoder.encode("1234"), User.Role.ADMIN));
        users.add(new User("guest", "guest@example.com", passwordEncoder.encode("1234"), User.Role.GUEST));
        try {
            userRepository.saveAll(users);
        } catch (Exception e) {
            log.debug("Test Data Exception:", e );
        }
        return userService.getUsers();
    }
}
