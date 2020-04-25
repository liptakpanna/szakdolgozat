package com.dna.application.backend.controller;

import com.dna.application.backend.TestDataGenerator;
import com.dna.application.backend.model.JwtRequest;
import com.dna.application.backend.model.UserRequest;
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
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AlignerControllerTest {
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
        addResearcher();
        researcherJwtToken = getTokenForUser("newname", "newpwd");
    }

    @Test
    public void a_getList_AdminJwt_EmptyList() {
        headers.set("Authorization", "Bearer " + adminJwtToken);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<?> response = restTemplate.exchange(
                apiUrl+"/align/list", HttpMethod.GET, entity, String.class);
        int header = response.getStatusCodeValue();
        Assert.assertEquals(200, header);
        String actual = response.toString();
        Assert.assertTrue(actual.contains("<200,[],"));
    }

    @Test
    public void getList_NoJwt_Denied() {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<?> response = restTemplate.exchange(
                apiUrl+"/align/list", HttpMethod.GET, entity, String.class);
        int header = response.getStatusCodeValue();
        Assert.assertEquals(403, header);
    }

    @Test
    public void getReferenceList_ResearcherJwt_ReturnList() {
        headers.set("Authorization", "Bearer " + researcherJwtToken);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<?> response = restTemplate.exchange(
                apiUrl+"/align/referencelist", HttpMethod.GET, entity, String.class);
        int header = response.getStatusCodeValue();
        Assert.assertEquals(200, header);
        String actual = response.toString();
        Assert.assertTrue(actual.contains("[{\"id\":1,\"name\":\"E. coli\",\"description\":\"Escherichia coli O104:H4 str. 2011C-3493, complete sequence\",\"filename\":\"ecoli\"}," +
                "{\"id\":2,\"name\":\"Bacillus Cereus\",\"description\":\"Bacillus cereus ATCC 14579 chromosome, complete genome\",\"filename\":\"bacillus\"}," +
                "{\"id\":3,\"name\":\"Staphylococcus Aureus\",\"description\":\"Staphylococcus aureus subsp. aureus NCTC 8325 chromosome, complete genome\",\"filename\":\"staphylococcus\"}," +
                "{\"id\":4,\"name\":\"COVID-19\",\"description\":\"Severe acute respiratory syndrome coronavirus 2 isolate Wuhan-Hu-1, complete genome\",\"filename\":\"covid_19\"}," +
                "{\"id\":5,\"name\":\"Rotavirus\",\"description\":\"Rotavirus A segment 1, complete genome\",\"filename\":\"rota\"}," +
                "{\"id\":6,\"name\":\"H1N1\",\"description\":\"Influenza A virus (A/Puerto Rico/8/1934(H1N1)) segment 1, complete sequence\",\"filename\":\"h1n1\"}]"));
    }

    private void addResearcher() {
        headers.set("Authorization", "Bearer " + adminJwtToken);
        HttpEntity<UserRequest> entity = new HttpEntity<>(testDataGenerator.getUserRequest(), headers);
        restTemplate.exchange(apiUrl+"/users/add", HttpMethod.POST, entity, String.class);
    }

    private String getTokenForUser(String username, String password) {
        JwtRequest request = new JwtRequest(username, password);
        HttpEntity<JwtRequest> entity = new HttpEntity<>(request, headers);
        ResponseEntity<?> response = restTemplate.exchange(
                apiUrl+"/authenticate", HttpMethod.POST, entity, String.class);
        String actual = response.toString();
        return actual.substring(actual.indexOf("token")+8, actual.indexOf("\"id\"")-2);
    }
}
