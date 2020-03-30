package com.dna.application.backend.service;

import com.dna.application.backend.dto.UserDto;
import com.dna.application.backend.exception.EntityNameAlreadyExistsException;
import com.dna.application.backend.model.User;
import com.dna.application.backend.model.UserRequest;
import com.dna.application.backend.model.UsernameListResponse;
import com.dna.application.backend.repository.AlignmentRepository;
import com.dna.application.backend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AlignmentRepository alignmentRepository;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private  ModelMapper modelMapper = new ModelMapper();

    public List<UserDto> getUsers() {
        List<User> users = userRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        users.removeIf(user -> user.getStatus() == User.Status.DELETED);

        Type listType = new TypeToken<List<UserDto>>() {}.getType();
        return modelMapper.map(users, listType);
    }

    public UsernameListResponse getUsernames(String requester) {
        List<String> usernames = userRepository.findUsernames();
        usernames.remove(requester);
        return new UsernameListResponse(usernames);
    }

    @Transactional
    public Boolean deleteUser(Long id, User user) throws Exception {
        if (user.getId().equals(id))
            throw new Exception("You cannot delete yourself.");
        User userForDelete = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id.toString()));
        userForDelete.setStatus(User.Status.DELETED);
        userForDelete.setAlignmentAccess(new HashSet<>());
        userRepository.saveAndFlush(userForDelete);
        return !userRepository.existsById(id);
    }

    @Transactional
    public boolean updateUser(UserRequest userRequest, String updater) throws Exception{
        Long id = userRequest.getId();
        if (id == null) throw new Exception("Id for updating not provided");

        String username = userRequest.getUsername();
        String email = userRequest.getEmail();
        String password = userRequest.getPassword();
        User.Role role = userRequest.getRole();
        User.Status status = userRequest.getStatus();

        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id.toString()));
        if(username != null){
            if(!username.equals(user.getUsername()) && userRepository.existsByUsername(username)) throw new EntityNameAlreadyExistsException();
            user.setUsername(username);
        }
        if(email != null) user.setEmail(email);
        if(password != null) user.setPassword(passwordEncoder.encode(password));
        if(role != null) user.setRole(role);
        if (status != null) user.setStatus(status);

        user.setUpdatedBy(updater);
        userRepository.saveAndFlush(user);

        return ((userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id.toString()))).getUpdatedBy().equals(updater));
    }

    public UserDto getUserDto(String username) {
        User user = userRepository.findByUsername(username);
        return modelMapper.map(user, UserDto.class);
    }

    public boolean addUser(UserRequest userRequest, String admin) throws Exception{
        if (userRequest.getPassword().equals(""))
            throw new Exception("Password required");
        if (userRequest.getUsername().length() > 12)
            throw new Exception("Username too long");
        if(userRepository.findByUsername(userRequest.getUsername()) != null)
            throw new EntityNameAlreadyExistsException();

        User newUser = modelMapper.map(userRequest , User.class);
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        newUser.setCreatedBy(admin);
        newUser.setStatus(User.Status.ENABLED);

        log.warn("{}", newUser);
        userRepository.saveAndFlush(newUser);
        return userRepository.existsByUsername(userRequest.getUsername());
    }
}
