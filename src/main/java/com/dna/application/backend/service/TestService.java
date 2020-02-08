package com.dna.application.backend.service;

import com.dna.application.backend.model.User;
import com.dna.application.backend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TestService {
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public boolean setTestData() {
        List<User> users = new ArrayList<>();
        users.add(new User("A","a@example.com","1234"));
        users.add(new User("B","b@example.com","1234"));
        try {
            userRepository.saveAll(users);
            userRepository.flush();
            return true;
        } catch (Exception e) {
            log.debug("Test Data Exception:", e );
            return false;
        }
    }
}
