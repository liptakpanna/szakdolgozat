package com.dna.application.backend.service;

import com.dna.application.backend.TestDataGenerator;
import com.dna.application.backend.dto.UserDto;
import com.dna.application.backend.exception.EntityNameAlreadyExistsException;
import com.dna.application.backend.model.User;
import com.dna.application.backend.model.UserRequest;
import com.dna.application.backend.model.UsernameListResponse;
import com.dna.application.backend.repository.UserRepository;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Sort;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    private TestDataGenerator testDataGenerator = new TestDataGenerator();

    @Test
    public void getUsers_NoUsers_EmptyList() {
        given(userRepository.findAll(Sort.by(Sort.Direction.ASC, "id")))
                .willReturn(new ArrayList<>());

        List<UserDto> result = userService.getUsers();
        Assert.assertEquals(new ArrayList<>(), result);
    }

    @Test
    public void getUsers_MultipleUsers_ReturnEnabled() {
        User deletedGuest = testDataGenerator.getGuest();
        deletedGuest.setStatus(User.Status.DELETED);
        List<User> repoResult = new ArrayList<>(Arrays.asList(testDataGenerator.getAdmin(), deletedGuest, testDataGenerator.getResearcher()));

        given(userRepository.findAll(Sort.by(Sort.Direction.ASC, "id")))
                .willReturn(repoResult);

        List<UserDto> result = userService.getUsers();
        Assert.assertEquals(Arrays.asList(testDataGenerator.getAdminDto(), testDataGenerator.getResearcherDto()), result);
    }

    @Test
    public void getUsername_MultipleUsers_RemoveRequester() {
        List<String> repoResult = new ArrayList<>(Arrays.asList("admin", "guest", "test"));
        given(userRepository.findUsernames())
                .willReturn(repoResult);
        UsernameListResponse result = userService.getUsernames("admin");
        Assert.assertTrue(result.getUsernames().contains("guest"));
        Assert.assertTrue(result.getUsernames().contains("test"));
        Assert.assertFalse(result.getUsernames().contains("admin"));
        Assert.assertEquals(2, result.getUsernames().size());
    }

    @Test
    public void deleteUser_DeleteSelf_Exception() throws Exception {
        exceptionRule.expect(Exception.class);
        exceptionRule.expectMessage("You cannot delete yourself.");
        userService.deleteUser(1L, testDataGenerator.getAdmin());
    }

    @Test(expected = EntityNotFoundException.class)
    public void deleteUser_NotFound_Exception() throws Exception {
        Long id = 3L;
        given(userRepository.findById(id))
                .willReturn(Optional.empty());
        userService.deleteUser(id, testDataGenerator.getAdmin());
    }

    @Test
    public void deleteUser_Existing_Successful() throws Exception {
        Long id = 2L;
        User user = testDataGenerator.getGuest();
        User deletedUser = testDataGenerator.getGuest();
        deletedUser.setStatus(User.Status.DELETED);
        given(userRepository.findById(id))
                .willReturn(Optional.of(user));
        given(userRepository.saveAndFlush(user))
                .willReturn(deletedUser);
        Assert.assertTrue(userService.deleteUser(id, testDataGenerator.getAdmin()));
    }

    @Test(expected = EntityNameAlreadyExistsException.class)
    public void updateUser_UsernameExists_Exception() throws Exception {
        UserRequest request = testDataGenerator.getUserRequest();
        User user = testDataGenerator.getGuest();
        Long id = user.getId();
        given(userRepository.findById(id))
                .willReturn(Optional.of(user));
        given(userRepository.existsByUsername("newname"))
                .willReturn(true);
        userService.updateUser(request, "updater");
    }

    @Test
    public void updateUser_ChangeEverything_Successful() throws Exception {
        UserRequest request = testDataGenerator.getUserRequest();
        User oldUser = testDataGenerator.getGuest();
        Long id = oldUser.getId();
        User updatedUser = testDataGenerator.getUpdatedUser();
        given(userRepository.findById(id))
                .willReturn(Optional.of(oldUser));
        given(userRepository.existsByUsername("newname"))
                .willReturn(false);
        given(userRepository.saveAndFlush(oldUser))
                .willReturn(updatedUser);
        Assert.assertTrue(userService.updateUser(request, "updater"));
    }

    @Test
    public void getUserDto_NameProvided_ReturnDto() {
        User user = testDataGenerator.getResearcher();
        UserDto userDto = testDataGenerator.getResearcherDto();
        given(userRepository.findByUsername(user.getUsername()))
                .willReturn(user);
        Assert.assertEquals(userDto, userService.getUserDto(user.getUsername()));
    }

    @Test(expected = EntityNameAlreadyExistsException.class)
    public void addUser_UsernameExists_Exception() throws Exception {
        UserRequest request = testDataGenerator.getUserRequest();
        given(userRepository.existsByUsername("newname"))
                .willReturn(true);
        userService.addUser(request, "admin");
    }

    @Test
    public void addUser_GoodRequest_Added() throws Exception {
        UserRequest request = testDataGenerator.getUserRequest();
        given(userRepository.existsByUsername("newname"))
                .willReturn(false);
        Assert.assertEquals("User saved" ,userService.addUser(request, "admin"));
    }



}
