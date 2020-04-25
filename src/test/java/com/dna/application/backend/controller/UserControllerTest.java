package com.dna.application.backend.controller;

import com.dna.application.backend.TestDataGenerator;
import com.dna.application.backend.model.JwtRequest;
import com.dna.application.backend.model.User;
import com.dna.application.backend.model.UserRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserControllerTest {
    @LocalServerPort
    private int port;

    TestRestTemplate restTemplate = new TestRestTemplate();
    HttpHeaders headers = new HttpHeaders();

    private TestDataGenerator testDataGenerator = new TestDataGenerator();

    private String adminJwtToken;
    private String researcherJwtToken;
    private String apiUrl;

    @Before
    public void setup() {
        apiUrl = "http://localhost:"+port+"/api";
        adminJwtToken = getTokenForUser("admin", "1234");
        researcherJwtToken = getTokenForUser("newname", "newpwd");
    }

    @Test
    public void a_getUsers_NoJwt_Denied() {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<?> response = restTemplate.exchange(
                apiUrl+"/users/list", HttpMethod.GET, entity, String.class);
        int header = response.getStatusCodeValue();
        Assert.assertEquals(403, header);
    }

    @Test
    public void b_getUsers_JwtAdmin_ReturnUsers() {
        headers.set("Authorization", "Bearer " + adminJwtToken);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<?> response = restTemplate.exchange(
                apiUrl+"/users/list", HttpMethod.GET, entity, String.class);
        String actual = response.toString();
        int header = response.getStatusCodeValue();
        Assert.assertEquals(200, header);
        Assert.assertTrue(actual.contains("[{\"id\":1,\"username\":\"admin\",\"email\":\"liptakpanna@gmail.com\",\"role\":\"ADMIN\""));
    }

    @Test
    public void c_postAdd_JwtAdmin_Added(){
        headers.set("Authorization", "Bearer " + adminJwtToken);
        HttpEntity<UserRequest> entity = new HttpEntity<>(testDataGenerator.getUserRequest(), headers);
        ResponseEntity<?> response = restTemplate.exchange(
                apiUrl+"/users/add", HttpMethod.POST, entity, String.class);
        String actual = response.toString();
        int header = response.getStatusCodeValue();
        Assert.assertEquals(200, header);
        Assert.assertTrue(actual.contains("User saved"));
    }

    @Test
    public void d_postAdd_JwtAdmin_NameExistException(){
        headers.set("Authorization", "Bearer " + adminJwtToken);
        HttpEntity<UserRequest> entity = new HttpEntity<>(testDataGenerator.getUserRequest(), headers);
        ResponseEntity<?> response = restTemplate.exchange(
                apiUrl+"/users/add", HttpMethod.POST, entity, String.class);
        String actual = response.toString();
        int header = response.getStatusCodeValue();
        Assert.assertEquals(500, header);
        Assert.assertTrue(actual.contains("Username already exists"));
    }


    @Test
    public void e_getUsers_JwtAdmin_ReturnTwoUsers() {
        headers.set("Authorization", "Bearer " + adminJwtToken);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<?> response = restTemplate.exchange(
                apiUrl+"/users/list", HttpMethod.GET, entity, String.class);
        String actual = response.toString();
        int header = response.getStatusCodeValue();
        Assert.assertEquals(200, header);
        Assert.assertTrue(actual.contains("[{\"id\":1,\"username\":\"admin\",\"email\":\"liptakpanna@gmail.com\",\"role\":\"ADMIN\""));
        Assert.assertTrue(actual.contains("\"id\":2,\"username\":\"newname\",\"email\":\"new@email.com\",\"role\":\"RESEARCHER"));
    }

    @Test
    public void f_getUsers_JwtResearcher_Denied() {
        headers.set("Authorization", "Bearer " + researcherJwtToken);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<?> response = restTemplate.exchange(
                apiUrl+"/users/list", HttpMethod.GET, entity, String.class);
        int header = response.getStatusCodeValue();
        Assert.assertEquals(403, header);
    }

    @Test
    public void g_getUsernamelist_ResearcherJwt_ReturnList() {
        headers.set("Authorization", "Bearer " + researcherJwtToken);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<?> response = restTemplate.exchange(
                apiUrl+"/users/usernamelist", HttpMethod.GET, entity, String.class);
        int header = response.getStatusCodeValue();
        Assert.assertEquals(200, header);
        String actual = response.toString();
        Assert.assertTrue(actual.contains("{\"usernames\":[\"admin\"]}"));
    }

    @Test
    public void h_putUpdate_AdminJwt_Updated(){
        headers.set("Authorization", "Bearer " + adminJwtToken);
        UserRequest userRequest = testDataGenerator.getUserRequest();
        userRequest.setRole(User.Role.GUEST);
        HttpEntity<UserRequest> entity = new HttpEntity<>(userRequest, headers);
        ResponseEntity<?> response = restTemplate.exchange(
                apiUrl+"/users/update", HttpMethod.PUT, entity, String.class);
        int header = response.getStatusCodeValue();
        Assert.assertEquals(200, header);
        String actual = response.toString();
        Assert.assertTrue(actual.contains("true"));
    }

    @Test
    public void i_putMeUpdate_ResearcherJwt_Updated(){
        headers.set("Authorization", "Bearer " + researcherJwtToken);
        UserRequest userRequest = testDataGenerator.getUserRequest();
        userRequest.setRole(null);
        userRequest.setUsername("testuser");
        HttpEntity<UserRequest> entity = new HttpEntity<>(userRequest, headers);
        ResponseEntity<?> response = restTemplate.exchange(
                apiUrl+"/users/me/update", HttpMethod.PUT, entity, String.class);
        int header = response.getStatusCodeValue();
        Assert.assertEquals(200, header);
        String actual = response.toString();
        Assert.assertTrue(actual.contains("true"));
    }

    @Test
    public void j_getlist_AdminJwt_ReturnUpdatedList() {
        headers.set("Authorization", "Bearer " + adminJwtToken);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<?> response = restTemplate.exchange(
                apiUrl+"/users/list", HttpMethod.GET, entity, String.class);
        int header = response.getStatusCodeValue();
        Assert.assertEquals(200, header);
        String actual = response.toString();
        Assert.assertTrue(actual.contains("[{\"id\":1,\"username\":\"admin\",\"email\":\"liptakpanna@gmail.com\",\"role\":\"ADMIN\""));
        Assert.assertTrue(actual.contains("\"id\":2,\"username\":\"testuser\",\"email\":\"new@email.com\",\"role\":\"GUEST"));
    }

    @Test
    public void k_deleteId_AdminJwt_Successful() {
        headers.set("Authorization", "Bearer " + adminJwtToken);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<?> response = restTemplate.exchange(
                apiUrl+"/users/delete/2", HttpMethod.DELETE, entity, String.class);
        String actual = response.toString();
        int header = response.getStatusCodeValue();
        Assert.assertEquals(200, header);
        Assert.assertTrue(actual.contains("true"));
    }

    @Test
    public void l_getUsernamelist_AdminJwt_EmptyList() {
        headers.set("Authorization", "Bearer " + adminJwtToken);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<?> response = restTemplate.exchange(
                apiUrl+"/users/usernamelist", HttpMethod.GET, entity, String.class);
        int header = response.getStatusCodeValue();
        Assert.assertEquals(200, header);
        String actual = response.toString();
        Assert.assertTrue(actual.contains("{\"usernames\":[]}"));
    }

    @Test
    public void m_getMe_AdminJwt_ReturnAdmin() {
        headers.set("Authorization", "Bearer " + adminJwtToken);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<?> response = restTemplate.exchange(
                apiUrl+"/users/me", HttpMethod.GET, entity, String.class);
        int header = response.getStatusCodeValue();
        Assert.assertEquals(200, header);
        String actual = response.toString();
        log.warn(actual);
        Assert.assertTrue(actual.contains("{\"id\":1,\"username\":\"admin\",\"email\":\"liptakpanna@gmail.com\",\"role\":\"ADMIN\",\"updatedBy\":null,"));
    }

    private String getTokenForUser(String username, String password) {
        JwtRequest request = new JwtRequest(username, password);
        HttpEntity<JwtRequest> entity = new HttpEntity<>(request, headers);
        ResponseEntity<?> response = restTemplate.exchange(
                apiUrl+"/authenticate", HttpMethod.POST, entity, String.class);
        String actual = response.toString();
        if(response.getStatusCodeValue() == 200)
            return actual.substring(actual.indexOf("token")+8, actual.indexOf("\"id\"")-2);
        else return null;
    }
}
