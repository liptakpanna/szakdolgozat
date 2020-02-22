package com.dna.application.backend.service;

import com.dna.application.backend.dto.UserDto;
import com.dna.application.backend.model.User;
import com.dna.application.backend.model.UserUpdateRequest;
import com.dna.application.backend.repository.UserRepository;
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
    public UserDto updateUser(UserUpdateRequest userUpdateRequest, String updater) throws Exception{
        boolean changed = false;

        Long id = userUpdateRequest.getId();
        if (id == null) throw new Exception("Id for updating not provided");
        String username = userUpdateRequest.getUsername();
        String email = userUpdateRequest.getEmail();
        String password = userUpdateRequest.getPassword();
        User.Role role = userUpdateRequest.getRole();

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
}
