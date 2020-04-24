package com.dna.application.backend;

import com.dna.application.backend.Application;
import com.dna.application.backend.controller.UserController;
import com.dna.application.backend.dto.UserDto;
import com.dna.application.backend.model.User;
import com.dna.application.backend.repository.AlignmentRepository;
import com.dna.application.backend.repository.UserRepository;
import com.dna.application.backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.*;

import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
@ContextConfiguration(classes = {UserService.class,})
public class MockUserControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private AlignmentRepository alignmentRepository;

    private JacksonTester<UserDto> jsonUserDto;
    private JacksonTester<List<UserDto>> listJsonTester;

    private TestDataGenerator testDataGenerator = new TestDataGenerator();

    @Before
    public void setup() {
        JacksonTester.initFields(this, new ObjectMapper());
    }

    @Test
    public void getUsers_NoUsers_EmptyList() throws Exception {
        given(userRepository.findAll()).willReturn(new ArrayList<>());
        MockHttpServletResponse response = mvc.perform(
                MockMvcRequestBuilders.get("/api/users/list")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        Assert.assertEquals( HttpStatus.OK.value(), response.getStatus());
        Assert.assertEquals( "[]", response.getContentAsString());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void getUsers_DeletedUser_EmptyList() throws Exception {
        User deletedUser = testDataGenerator.getGuest();
        deletedUser.setStatus(User.Status.DELETED);
        given(userRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))).willReturn(Collections.singletonList(deletedUser));
        MockHttpServletResponse response = mvc.perform(
                MockMvcRequestBuilders.get("/api/users/list")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        Assert.assertEquals( HttpStatus.OK.value(), response.getStatus());
        Assert.assertEquals( "[]", response.getContentAsString());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void getUsers_TwoUsers_CheckList() throws Exception {
        Date timestamp = new Date();
        User user1 = testDataGenerator.getAdmin();
        UserDto userDto = new UserDto( 1L,"admin","admin@email.com", User.Role.ADMIN, "updater", timestamp, timestamp,"creator" );
        //User user2 = new User("guest","guest@email.com", User.Role.GUEST, User.Status.ENABLED);
        given(userRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))).willReturn(Arrays.asList(user1));
        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.get("/api/users/list")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse();
        Assert.assertEquals( HttpStatus.OK.value(), response.getStatus());
        Assert.assertEquals( listJsonTester.write(Arrays.asList(userDto)), response.getContentAsString());
    }
}
