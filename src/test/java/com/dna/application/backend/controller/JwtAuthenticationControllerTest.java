package com.dna.application.backend.controller;

import com.dna.application.backend.model.JwtRequest;
import com.dna.application.backend.model.JwtResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JwtAuthenticationControllerTest {
    @LocalServerPort
    private int port;

    TestRestTemplate restTemplate = new TestRestTemplate();
    HttpHeaders headers = new HttpHeaders();

    private JwtRequest request = new JwtRequest("admin", "1234");

    private String jwtToken;
    private String apiUrl;

    @Before
    public void setup() {
        apiUrl = "http://localhost:"+port+"/api";
        JwtRequest request = new JwtRequest("admin", "1234");
        HttpEntity<JwtRequest> entity = new HttpEntity<>(request, headers);
        ResponseEntity<?> response = restTemplate.exchange(
                apiUrl+"/authenticate", HttpMethod.POST, entity, String.class);
        String actual = response.toString();
        jwtToken = actual.substring(actual.indexOf("token")+8, actual.indexOf("\"id\"")-2);
    }

    @Test
    public void postAuthenticate_UserExists_GetResponse() {
        HttpEntity<JwtRequest> entity = new HttpEntity<>(request, headers);
        ResponseEntity<?> response = restTemplate.exchange(
                apiUrl + "/authenticate", HttpMethod.POST, entity, String.class);
        String actual = response.toString();
        int header = response.getStatusCodeValue();
        Assert.assertEquals(200, header);
        Assert.assertTrue(actual.contains("\"jwttoken\":\""));
        Assert.assertTrue(actual.contains("\"id\":1,\"role\":\"ADMIN\""));
    }

    @Test
    public void postAuthenticate_NoUser_Denied() {
        request.setUsername("notexisting");
        HttpEntity<JwtRequest> entity = new HttpEntity<>(request, headers);
        ResponseEntity<?> response = restTemplate.exchange(
                apiUrl + "/authenticate", HttpMethod.POST, entity, String.class);
        int header = response.getStatusCodeValue();
        Assert.assertEquals(403, header);
    }

    @Test
    public void getValidate_ValidToken_ReturnTrue() {
        headers.set("Authorization", "Bearer " + jwtToken);
        HttpEntity<JwtRequest> entity = new HttpEntity<>(null, headers);
        ResponseEntity<?> response = restTemplate.exchange(
                apiUrl + "/validate", HttpMethod.GET, entity, String.class);
        String actual = response.toString();
        Assert.assertTrue(actual.contains("<200,true,"));
    }

    @Test
    public void getValidate_BadToken_Denied() {
        String badToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        headers.set("Authorization", "Bearer " + badToken);
        HttpEntity<JwtRequest> entity = new HttpEntity<>(null, headers);
        ResponseEntity<?> response = restTemplate.exchange(
                apiUrl + "/validate", HttpMethod.GET, entity, String.class);
        int header = response.getStatusCodeValue();
        Assert.assertEquals(403, header);
    }

    @Test
    public void getForgotPassword_ValidToken_ReturnAdminEmail() {
        headers.set("Authorization", "Bearer " + jwtToken);
        HttpEntity<JwtRequest> entity = new HttpEntity<>(null, headers);
        ResponseEntity<?> response = restTemplate.exchange(
                apiUrl + "/forgotpassword", HttpMethod.GET, entity, String.class);
        String actual = response.toString();
        Assert.assertTrue(actual.contains("<200,[\"liptakpanna@gmail.com\"],"));
    }
}
