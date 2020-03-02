package com.dna.application.backend.service;

import com.dna.application.backend.dto.UserDto;
import com.dna.application.backend.model.User;
import com.dna.application.backend.model.UserRequest;
import com.dna.application.backend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.lang.reflect.Type;
import java.util.List;

@Slf4j
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private  ModelMapper modelMapper = new ModelMapper();

    public List<UserDto> getUsers() {
        List<User> users = userRepository.findAll();

        Type listType = new TypeToken<List<UserDto>>() {}.getType();
        return modelMapper.map(users, listType);
    }

    public List<UserDto> deleteUser(Long id) {
        userRepository.deleteById(id);
        userRepository.flush();
        return getUsers();
    }

    @Transactional
    public UserDto updateUser(UserRequest userRequest, String updater) throws Exception{
        boolean changed = false;

        Long id = userRequest.getId();
        if (id == null) throw new Exception("Id for updating not provided");
        String username = userRequest.getUsername();
        String email = userRequest.getEmail();
        String password = userRequest.getPassword();
        User.Role role = userRequest.getRole();

        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id.toString()));
        if(username != null) {
            user.setUsername(username);
            changed = true;
        }
        if(email != null) {
            user.setEmail(email);
            changed = true;
        }
        if(password != null) {
            user.setPassword(passwordEncoder.encode(password));
            changed = true;
        }
        if(role != null) {
            user.setRole(role);
            changed = true;
        }

        if (changed) {
            user.setUpdatedBy(updater);
            userRepository.saveAndFlush(user);
        }

        return modelMapper.map(user, UserDto.class);
    }

    public UserDto getUser(String username) {
        User user = userRepository.findByUsername(username);
        return modelMapper.map(user, UserDto.class);
    }

    public List<UserDto> addUser(UserRequest userRequest) throws Exception{
        if (userRequest.getPassword().equals(""))
            throw new Exception("Cannot save this user: Password required");
        if (userRequest.getUsername().length() > 12)
            throw new Exception("Cannot save this user: Username too long");
        if(userRepository.findByUsername(userRequest.getUsername()) != null)
            throw new Exception("Cannot save this user: Username already in use");

        log.warn("{}", userRequest);

        User newUser = modelMapper.map(userRequest , User.class);
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));

        log.warn("{}", newUser);
        userRepository.saveAndFlush(newUser);
        return getUsers();
    }
}
