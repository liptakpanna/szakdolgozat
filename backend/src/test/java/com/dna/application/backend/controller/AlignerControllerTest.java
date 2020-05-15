package com.dna.application.backend.controller;

import com.dna.application.backend.TestDataGenerator;
import com.dna.application.backend.model.AlignmentRequest;
import com.dna.application.backend.model.JwtRequest;
import com.dna.application.backend.model.UserRequest;
import com.dna.application.backend.util.BaseCommandRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AlignerControllerTest extends BaseCommandRunner {
    @LocalServerPort
    private int port;

    TestRestTemplate restTemplate = new TestRestTemplate();
    HttpHeaders headers = new HttpHeaders();

    private TestDataGenerator testDataGenerator = new TestDataGenerator();

    private String adminJwtToken;
    private String researcherJwtToken;
    private String apiUrl;
    private String testFolder = testDataGenerator.getTestFolder();

    @Before
    public void setup() {
        apiUrl = "http://localhost:"+port+"/api";
        adminJwtToken = getTokenForUser("admin", "1234");
        addResearcher();
        researcherJwtToken = getTokenForUser("researcher", "newpwd");
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
    public void b_postAlign_AdminJwtSingleNoRefBowtie_DoAlignment() throws Exception {
        headers.set("Authorization", "Bearer " + adminJwtToken);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(getRequest(), headers);
        ResponseEntity<?> response = restTemplate.exchange(
                apiUrl+"/align", HttpMethod.POST, entity, String.class);
        int header = response.getStatusCodeValue();
        Assert.assertEquals(200, header);
        String actual = response.toString();
        Assert.assertTrue(actual.contains("{\"id\":1,\"name\":\"integr test\",\"aligner\":\"BOWTIE\"," +
                "\"description\":\"Lorem ipsum dosum\"," +
                "\"referenceUrl\":\"http://localhost:9090/resources/files/references/integr_test.fna\"," +
                "\"bamUrls\":[{\"id\":1,\"name\":\"test track\"," +
                "\"url\":\"http://localhost:9090/resources/files/bams/integr_test1.bam\"}],\"visibility\":\"PRIVATE\"," +
                "\"owner\":\"admin\","));
        Assert.assertTrue(actual.contains("\"updatedAt\":null,\"updatedBy\":null,\"userAccess\":[]"));
        String filesExistMessage = runCommand(new String[]{"ls", "-la", testFolder+"bams/integr_test1.bam", testFolder+"bams/integr_test1.bam.bai"
                ,testFolder+"references/integr_test.fna", testFolder+"references/integr_test.fna.fai" });
        Assert.assertFalse(filesExistMessage.toLowerCase().contains("no such file or directory"));
    }

    @Test
    public void c_postAlign_AdminJwtSameName_NameExists() {
        headers.set("Authorization", "Bearer " + adminJwtToken);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(getRequest(), headers);
        ResponseEntity<?> response = restTemplate.exchange(
                apiUrl+"/align", HttpMethod.POST, entity, String.class);
        int header = response.getStatusCodeValue();
        Assert.assertEquals(500, header);
        String actual = response.toString();
        Assert.assertTrue(actual.contains("Alignment name already exists."));
    }

    @Test
    public void d_getList_ResearcherJwt_EmptyList() {
        headers.set("Authorization", "Bearer " + researcherJwtToken);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<?> response = restTemplate.exchange(
                apiUrl+"/align/list", HttpMethod.GET, entity, String.class);
        int header = response.getStatusCodeValue();
        Assert.assertEquals(200, header);
        String actual = response.toString();
        Assert.assertTrue(actual.contains("<200,[],"));
    }

    @Test
    public void e_putUpdate_AdminJwtChangeNameResAccess_RenamedUpdated() throws Exception {
        headers.set("Authorization", "Bearer " + adminJwtToken);
        AlignmentRequest request = new AlignmentRequest();
        request.setId(1L);
        request.setName("new name");
        request.setUsernameAccessList(Collections.singletonList("researcher"));
        HttpEntity<AlignmentRequest> entity = new HttpEntity<>(request, headers);
        ResponseEntity<?> response = restTemplate.exchange(
                apiUrl+"/align/update", HttpMethod.PUT, entity, String.class);
        int header = response.getStatusCodeValue();
        Assert.assertEquals(200, header);
        String actual = response.toString();
        Assert.assertTrue(actual.contains("{\"id\":1,\"name\":\"new name\",\"aligner\":\"BOWTIE\"," +
                "\"description\":\"Lorem ipsum dosum\"," +
                "\"referenceUrl\":\"http://localhost:9090/resources/files/references/new_name.fna\"," +
                "\"bamUrls\":[{\"id\":1,\"name\":\"test track\"," +
                "\"url\":\"http://localhost:9090/resources/files/bams/new_name1.bam\"}],\"visibility\":\"PRIVATE\"," +
                "\"owner\":\"admin\","));
        Assert.assertTrue(actual.contains("\"updatedBy\":\"admin\",\"userAccess\":[\"researcher\"]"));
        String filesExistMessage = runCommand(new String[]{"ls", "-la", testFolder+"bams/new_name1.bam", testFolder+"bams/new_name1.bam.bai"
                ,testFolder+"references/new_name.fna", testFolder+"references/new_name.fna.fai" });
        Assert.assertFalse(filesExistMessage.toLowerCase().contains("no such file or directory"));
    }

    @Test
    public void f_getList_ResearcherJwt_Accessed() {
        headers.set("Authorization", "Bearer " + researcherJwtToken);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<?> response = restTemplate.exchange(
                apiUrl+"/align/list", HttpMethod.GET, entity, String.class);
        int header = response.getStatusCodeValue();
        Assert.assertEquals(200, header);
        String actual = response.toString();
        Assert.assertTrue(actual.contains("{\"id\":1,\"name\":\"new name\",\"aligner\":\"BOWTIE\"," +
                "\"description\":\"Lorem ipsum dosum\"," +
                "\"referenceUrl\":\"http://localhost:9090/resources/files/references/new_name.fna\"," +
                "\"bamUrls\":[{\"id\":1,\"name\":\"test track\"," +
                "\"url\":\"http://localhost:9090/resources/files/bams/new_name1.bam\"}],\"visibility\":\"PRIVATE\"," +
                "\"owner\":\"admin\","));
        Assert.assertTrue(actual.contains("\"updatedBy\":\"admin\",\"userAccess\":[\"researcher\"]"));
    }

    @Test
    public void g_postAlign_AdminJwtNotFastq_WrongFileType() {
        headers.set("Authorization", "Bearer " + adminJwtToken);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> request = getRequest();
        request.set("name", "test");
        request.set("readsForDna[0].read1", new FileSystemResource(testFolder+"reads/wrongfile.txt"));
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(request, headers);
        ResponseEntity<?> response = restTemplate.exchange(
                apiUrl + "/align", HttpMethod.POST, entity, String.class);
        int header = response.getStatusCodeValue();
        Assert.assertEquals(500, header);
        String actual = response.toString();
        Assert.assertTrue(actual.contains("Wrong file type."));
    }

    @Test
    public void h_postAlign_ResearcherPairedRefBwa_doAlignment() throws Exception {
        headers.set("Authorization", "Bearer " + researcherJwtToken);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> request = getRequest();
        request.set("name", "test bwa");
        request.set("referenceId", 1L);
        request.set("readsForDna[0].isPaired", "true");
        request.set("aligner", "BWA");
        request.set("visibility", "PRIVATE_GROUP");
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(request, headers);
        ResponseEntity<?> response = restTemplate.exchange(
                apiUrl + "/align", HttpMethod.POST, entity, String.class);
        int header = response.getStatusCodeValue();
        Assert.assertEquals(200, header);
        String actual = response.toString();
        Assert.assertTrue(actual.contains("{\"id\":2,\"name\":\"test bwa\",\"aligner\":\"BWA\"," +
                "\"description\":\"Lorem ipsum dosum\"," +
                "\"referenceUrl\":\"http://localhost:9090/resources/files/examples/ecoli.fna\"," +
                "\"bamUrls\":[{\"id\":2,\"name\":\"test track\"," +
                "\"url\":\"http://localhost:9090/resources/files/bams/test_bwa1.bam\"}" +
                "],\"visibility\":\"PRIVATE_GROUP\"," +
                "\"owner\":\"researcher\","));
        Assert.assertTrue(actual.contains("\"updatedAt\":null,\"updatedBy\":null,\"userAccess\":[]"));
        String filesExistMessage = runCommand(new String[]{"ls", "-la", testFolder+"bams/test_bwa1.bam", testFolder+"bams/test_bwa1.bam.bai"});
        Assert.assertFalse(filesExistMessage.toLowerCase().contains("no such file or directory"));

    }

    @Test
    public void i_getList_AdminJwt_PrivateGroupNotAccessed() {
        headers.set("Authorization", "Bearer " + adminJwtToken);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<?> response = restTemplate.exchange(
                apiUrl+"/align/list", HttpMethod.GET, entity, String.class);
        int header = response.getStatusCodeValue();
        Assert.assertEquals(200, header);
        String actual = response.toString();
        Assert.assertFalse(actual.contains("{\"id\":2,\"name\":\"test bwa\",\"aligner\":\"BWA\"," +
                "\"description\":\"Lorem ipsum dosum\"," +
                "\"referenceUrl\":\"http://localhost:9090/resources/files/examples/ecoli.fna\"," +
                "\"bamUrls\":[{\"id\":2,\"name\":\"test track\"," +
                "\"url\":\"http://localhost:9090/resources/files/bams/test_bwa1.bam\"}" +
                "],\"visibility\":\"PRIVATE_GROUP\"," +
                "\"owner\":\"researcher\","));
    }

    @Test
    public void i_getList_ResearcherJwt_PrivateGroupAccessed() {
        headers.set("Authorization", "Bearer " + researcherJwtToken);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<?> response = restTemplate.exchange(
                apiUrl+"/align/list", HttpMethod.GET, entity, String.class);
        int header = response.getStatusCodeValue();
        Assert.assertEquals(200, header);
        String actual = response.toString();
        Assert.assertTrue(actual.contains("{\"id\":2,\"name\":\"test bwa\",\"aligner\":\"BWA\"," +
                "\"description\":\"Lorem ipsum dosum\"," +
                "\"referenceUrl\":\"http://localhost:9090/resources/files/examples/ecoli.fna\"," +
                "\"bamUrls\":[{\"id\":2,\"name\":\"test track\"," +
                "\"url\":\"http://localhost:9090/resources/files/bams/test_bwa1.bam\"}" +
                "],\"visibility\":\"PRIVATE_GROUP\"," +
                "\"owner\":\"researcher\","));
        Assert.assertTrue(actual.contains("{\"id\":1,\"name\":\"new name\",\"aligner\":\"BOWTIE\"," +
                "\"description\":\"Lorem ipsum dosum\"," +
                "\"referenceUrl\":\"http://localhost:9090/resources/files/references/new_name.fna\"," +
                "\"bamUrls\":[{\"id\":1,\"name\":\"test track\"," +
                "\"url\":\"http://localhost:9090/resources/files/bams/new_name1.bam\"}],\"visibility\":\"PRIVATE\"," +
                "\"owner\":\"admin\","));
    }

    @Test
    public void j_deleteId_ResearcherOwned_Deleted() {
        headers.set("Authorization", "Bearer " + researcherJwtToken);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<?> response = restTemplate.exchange(
                apiUrl+"/align/delete/2", HttpMethod.DELETE, entity, String.class);
        int header = response.getStatusCodeValue();
        Assert.assertEquals(200, header);
        String actual = response.toString();
        Assert.assertTrue(actual.contains("true"));
    }

    @Test
    public void k_getList_ResearcherJwt_DeletedNotReturned() {
        headers.set("Authorization", "Bearer " + researcherJwtToken);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<?> response = restTemplate.exchange(
                apiUrl+"/align/list", HttpMethod.GET, entity, String.class);
        int header = response.getStatusCodeValue();
        Assert.assertEquals(200, header);
        String actual = response.toString();
        Assert.assertFalse(actual.contains("{\"id\":2,\"name\":\"test bwa\",\"aligner\":\"BWA\"," +
                "\"description\":\"Lorem ipsum dosum\"," +
                "\"referenceUrl\":\"http://localhost:9090/resources/files/examples/ecoli.fna\"," +
                "\"bamUrls\":[{\"id\":2,\"name\":\"test track\"," +
                "\"url\":\"http://localhost:9090/resources/files/bams/test_bwa1.bam\"}" +
                "],\"visibility\":\"PRIVATE_GROUP\"," +
                "\"owner\":\"researcher\","));
        Assert.assertTrue(actual.contains("{\"id\":1,\"name\":\"new name\",\"aligner\":\"BOWTIE\"," +
                "\"description\":\"Lorem ipsum dosum\"," +
                "\"referenceUrl\":\"http://localhost:9090/resources/files/references/new_name.fna\"," +
                "\"bamUrls\":[{\"id\":1,\"name\":\"test track\"," +
                "\"url\":\"http://localhost:9090/resources/files/bams/new_name1.bam\"}],\"visibility\":\"PRIVATE\"," +
                "\"owner\":\"admin\","));
    }

    @Test
    public void l_postAlign_ResearcherSingleNoRefSnapAdminAccess_doAlignment() throws Exception {
        headers.set("Authorization", "Bearer " + researcherJwtToken);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> request = getRequest();
        request.set("name", "test snap");
        request.set("referenceId", null);
        request.set("readsForDna[0].isPaired", "false");
        request.set("aligner", "SNAP");
        request.set("visibility", "PRIVATE_GROUP");
        request.set("usernameAccessList", "admin");
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(request, headers);
        ResponseEntity<?> response = restTemplate.exchange(
                apiUrl + "/align", HttpMethod.POST, entity, String.class);
        int header = response.getStatusCodeValue();
        Assert.assertEquals(200, header);
        String actual = response.toString();
        Assert.assertTrue(actual.contains("{\"id\":3,\"name\":\"test snap\",\"aligner\":\"SNAP\"," +
                "\"description\":\"Lorem ipsum dosum\"," +
                "\"referenceUrl\":\"http://localhost:9090/resources/files/references/test_snap.fna\"," +
                "\"bamUrls\":[{\"id\":3,\"name\":\"test track\"," +
                "\"url\":\"http://localhost:9090/resources/files/bams/test_snap1.bam\"}" +
                "],\"visibility\":\"PRIVATE_GROUP\"," +
                "\"owner\":\"researcher\","));
        Assert.assertTrue(actual.contains("\"updatedAt\":null,\"updatedBy\":null,\"userAccess\":[\"admin\"]"));
        String filesExistMessage = runCommand(new String[]{"ls", "-la", testFolder+"bams/test_snap1.bam", testFolder+"bams/test_snap1.bam.bai",
                testFolder+"references/test_snap.fna",testFolder+"references/test_snap.fna.fai"});
        Assert.assertFalse(filesExistMessage.toLowerCase().contains("no such file or directory"));

    }

    @Test
    public void m_getList_AdminJwt_PrivateGroupAccessed() {
        headers.set("Authorization", "Bearer " + adminJwtToken);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<?> response = restTemplate.exchange(
                apiUrl+"/align/list", HttpMethod.GET, entity, String.class);
        int header = response.getStatusCodeValue();
        Assert.assertEquals(200, header);
        String actual = response.toString();
        Assert.assertTrue(actual.contains("{\"id\":3,\"name\":\"test snap\",\"aligner\":\"SNAP\"," +
                "\"description\":\"Lorem ipsum dosum\"," +
                "\"referenceUrl\":\"http://localhost:9090/resources/files/references/test_snap.fna\"," +
                "\"bamUrls\":[{\"id\":3,\"name\":\"test track\"," +
                "\"url\":\"http://localhost:9090/resources/files/bams/test_snap1.bam\"}" +
                "],\"visibility\":\"PRIVATE_GROUP\"," +
                "\"owner\":\"researcher\","));
        Assert.assertTrue(actual.contains("{\"id\":1,\"name\":\"new name\",\"aligner\":\"BOWTIE\"," +
                "\"description\":\"Lorem ipsum dosum\"," +
                "\"referenceUrl\":\"http://localhost:9090/resources/files/references/new_name.fna\"," +
                "\"bamUrls\":[{\"id\":1,\"name\":\"test track\"," +
                "\"url\":\"http://localhost:9090/resources/files/bams/new_name1.bam\"}],\"visibility\":\"PRIVATE\"," +
                "\"owner\":\"admin\","));
    }

    @Test
    public void n_putUpdate_ResearcherJwtChangeName_NameExists() throws Exception {
        headers.set("Authorization", "Bearer " + researcherJwtToken);
        AlignmentRequest request = new AlignmentRequest();
        request.setId(3L);
        request.setName("new name");
        HttpEntity<AlignmentRequest> entity = new HttpEntity<>(request, headers);
        ResponseEntity<?> response = restTemplate.exchange(
                apiUrl+"/align/update", HttpMethod.PUT, entity, String.class);
        int header = response.getStatusCodeValue();
        Assert.assertEquals(500, header);
        String actual = response.toString();
        Assert.assertTrue(actual.contains("Alignment name already exists."));
    }

    @Test
    public void o_putUpdate_AdminJwtRemoveResAccess_Updated() throws Exception {
        headers.set("Authorization", "Bearer " + adminJwtToken);
        AlignmentRequest request = new AlignmentRequest();
        request.setId(1L);
        request.setUsernameAccessList(new ArrayList<>());
        HttpEntity<AlignmentRequest> entity = new HttpEntity<>(request, headers);
        ResponseEntity<?> response = restTemplate.exchange(
                apiUrl+"/align/update", HttpMethod.PUT, entity, String.class);
        int header = response.getStatusCodeValue();
        Assert.assertEquals(200, header);
        String actual = response.toString();
        Assert.assertTrue(actual.contains("\"updatedBy\":\"admin\",\"userAccess\":[]"));
    }

    @Test
    public void p_getList_ResearcherJwt_NotAccessed() {
        headers.set("Authorization", "Bearer " + researcherJwtToken);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<?> response = restTemplate.exchange(
                apiUrl+"/align/list", HttpMethod.GET, entity, String.class);
        int header = response.getStatusCodeValue();
        Assert.assertEquals(200, header);
        String actual = response.toString();
        Assert.assertFalse(actual.contains("{\"id\":1,\"name\":\"new name\",\"aligner\":\"BOWTIE\"," +
                "\"description\":\"Lorem ipsum dosum\"," +
                "\"referenceUrl\":\"http://localhost:9090/resources/files/references/new_name.fna\"," +
                "\"bamUrls\":[{\"id\":1,\"name\":\"test track\"," +
                "\"url\":\"http://localhost:9090/resources/files/bams/new_name1.bam\"}],\"visibility\":\"PRIVATE\"," +
                "\"owner\":\"admin\","));
    }

    @Test
    public void getList_NoJwt_Denied() {
        headers.set("Authorization", null);
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
        UserRequest userRequest = testDataGenerator.getUserRequest();
        userRequest.setUsername("researcher");
        HttpEntity<UserRequest> entity = new HttpEntity<>(userRequest, headers);
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

    private MultiValueMap<String, Object> getRequest() {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("readsForDna[0].name", "test track");
        body.add("readsForDna[0].isPaired", "false");
        body.add("readsForDna[0].validCount", "all");
        body.add("readsForDna[0].mismatch", "1");
        body.add("readsForDna[0].maxHits", "100");
        body.add("readsForDna[0].maxDist", "10");
        body.add("readsForDna[0].penalties", Arrays.asList("4","11", "6"));
        body.add("readsForDna[0].read1", new FileSystemResource(testFolder+"reads/reads.fastq"));
        body.add("readsForDna[0].read2", new FileSystemResource(testFolder+"reads/reads2.fastq"));
        body.add("referenceDna", new FileSystemResource(testFolder+"examples/ecoli.fna"));
        body.add("aligner", "BOWTIE");
        body.add("name", "integr test");
        body.add("description", "Lorem ipsum dosum");
        body.add("visibility", "PRIVATE");
        body.add("usernameAccessList", null);
        return body;
    }
}
