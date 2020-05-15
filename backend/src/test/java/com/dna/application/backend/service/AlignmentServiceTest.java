package com.dna.application.backend.service;

import com.dna.application.backend.TestDataGenerator;
import com.dna.application.backend.dto.AlignmentDto;
import com.dna.application.backend.exception.EntityNameAlreadyExistsException;
import com.dna.application.backend.model.Alignment;
import com.dna.application.backend.model.AlignmentRequest;
import com.dna.application.backend.model.User;
import com.dna.application.backend.repository.AlignmentRepository;
import com.dna.application.backend.repository.BamUrlRepository;
import com.dna.application.backend.repository.UserRepository;
import com.dna.application.backend.util.BaseCommandRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class AlignmentServiceTest extends BaseCommandRunner {
    @Mock
    private AlignmentRepository alignmentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BamUrlRepository bamUrlRepository;

    @InjectMocks
    private AlignmentService alignmentService;

    private TestDataGenerator testDataGenerator = new TestDataGenerator();

    private String testFolder = testDataGenerator.getTestFolder();

    @Test
    public void getAlignments_GuestVisPublicOwnerDeleted_ReturnAlignment() throws Exception{
        User user = testDataGenerator.getGuest();
        Alignment alPub = testDataGenerator.getPublicAlignmentDeletedOwner();
        AlignmentDto expected = testDataGenerator.getPublicAlignmentDtoDeletedOwner();
        given(alignmentRepository.findByVisibility(Alignment.Visibility.PUBLIC))
                .willReturn(Collections.singletonList(alPub));
        given(alignmentRepository.findByName(alPub.getName()))
                .willReturn(alPub);
        Assert.assertEquals(Collections.singletonList(expected), alignmentService.getAlignments(user));
    }

    @Test
    public void getAlignments_AdminVisPrivate_ReturnAlignment() throws Exception{
        User user = testDataGenerator.getAdmin();
        Alignment alPriv = testDataGenerator.getPrivateAlignment();
        AlignmentDto expected =  testDataGenerator.getPrivateAlignmentDto();

        given(alignmentRepository.findByVisibility(Alignment.Visibility.PUBLIC))
                .willReturn(new ArrayList<>());
        given(alignmentRepository.findByVisibility(Alignment.Visibility.PRIVATE))
                .willReturn(Collections.singletonList(alPriv));
        given(alignmentRepository.findByName(alPriv.getName()))
                .willReturn(alPriv);
        Assert.assertEquals(Collections.singletonList(expected), alignmentService.getAlignments(user));
    }

    @Test
    public void getAlignments_AdminAccessPrivGroup_ReturnAlignment() throws Exception{
        User user = testDataGenerator.getAdmin();
        Alignment alPrivGr = testDataGenerator.getPrivateGroupAlignment();
        user.setAlignmentAccess(new HashSet<>(Collections.singletonList(alPrivGr)));
        AlignmentDto expected = testDataGenerator.getPrivateGroupAlignmentDto();

        given(alignmentRepository.findByVisibility(Alignment.Visibility.PUBLIC))
                .willReturn(new ArrayList<>());
        given(alignmentRepository.findByVisibility(Alignment.Visibility.PRIVATE))
                .willReturn(new ArrayList<>());
        given(alignmentRepository.findByName(alPrivGr.getName()))
                .willReturn(alPrivGr);
        Assert.assertEquals(Collections.singletonList(expected), alignmentService.getAlignments(user));
    }

    @Test
    public void deleteAlignment_Existing_DeleteFiles() throws Exception {
        ReflectionTestUtils.setField(alignmentService, "folder", testFolder);
        runCommand(new String[]{"touch", testFolder+"bams/a1.bam", testFolder+"bams/a1.bam.bai"});
        User user = testDataGenerator.getResearcher();
        User guest = testDataGenerator.getGuest();
        Alignment al = testDataGenerator.getPrivateAlignment();
        al.setUserAccess(new HashSet<>(Collections.singletonList(guest)));
        given(alignmentRepository.findById(al.getId()))
                .willReturn(Optional.of(al));
        given(alignmentRepository.existsById(al.getId()))
                .willReturn(true);

        Assert.assertFalse(alignmentService.deleteAlignment(al.getId(), user));
        String result = runCommand(new String[]{"ls", "-la", testFolder+"bams/a1.bam", testFolder+"bams/a1.bam.bai"});
        Assert.assertTrue(result.toLowerCase().contains("no such file or directory"));
    }

    @Test(expected = EntityNameAlreadyExistsException.class)
    public void updateAlignment_NameExists_Exception() throws Exception {
        User user = testDataGenerator.getResearcher();
        Alignment oldAl = testDataGenerator.getPublicAlignmentDeletedOwner();
        AlignmentRequest request = testDataGenerator.getAlignmentRequest();

        given(alignmentRepository.findById(oldAl.getId()))
                .willReturn(Optional.of(oldAl));
        given(alignmentRepository.existsByName(request.getName()))
                .willReturn(true);

      alignmentService.updateAlignment(request, user);

    }

    @Test
    public void updateAlignment_FieldsProvided_returnDto() throws Exception{
        ReflectionTestUtils.setField(alignmentService, "folder", testFolder);
        runCommand(new String[]{"touch", testFolder+"bams/a1.bam", testFolder+"bams/a1.bam.bai",
                testFolder+"references/a.fna", testFolder+"references/a.fna.fai"});
        User user = testDataGenerator.getResearcher();
        Alignment oldAl = testDataGenerator.getPublicAlignmentDeletedOwner();
        Alignment updatedAl = testDataGenerator.getUpdatedAlignment();
        AlignmentRequest request = testDataGenerator.getAlignmentRequest();
        AlignmentDto expected = testDataGenerator.getUpdatedAlignmentDto();

        given(alignmentRepository.findById(oldAl.getId()))
                .willReturn(Optional.of(oldAl));
        given(alignmentRepository.existsByName(request.getName()))
                .willReturn(false);
        given(alignmentRepository.findByName(updatedAl.getName()))
                .willReturn(updatedAl);

        Assert.assertEquals(expected, alignmentService.updateAlignment(request, user));
        String oldFiles = runCommand(new String[]{"ls", "-la", testFolder+"bams/a1.bam"});
        Assert.assertTrue(oldFiles.toLowerCase().contains("no such file or directory"));
        String updatedFiles = runCommand(new String[]{"ls", "-la", testFolder+"bams/b1.bam", testFolder+"bams/b1.bam.bai", testFolder+"references/b.fna", testFolder+"references/b.fna.fai"});
        Assert.assertFalse(updatedFiles.toLowerCase().contains("no such file or directory"));
    }

    @Test
    public void updateAlignment_RemoveUser_returnDto() throws Exception{
        User user = testDataGenerator.getResearcher();
        User guest = testDataGenerator.getGuest();
        Alignment oldAl = testDataGenerator.getPublicAlignmentDeletedOwner();
        oldAl.getUserAccess().add(guest);
        Alignment updatedAl = testDataGenerator.getUpdatedAlignment();
        updatedAl.setName(oldAl.getName());
        AlignmentRequest request = testDataGenerator.getAlignmentRequest();
        request.setUsernameAccessList(new ArrayList<>());
        request.setName(oldAl.getName());
        AlignmentDto expected = testDataGenerator.getUpdatedAlignmentDto();
        expected.setName(oldAl.getName());

        given(alignmentRepository.findById(oldAl.getId()))
                .willReturn(Optional.of(oldAl));
        given(alignmentRepository.findByName(updatedAl.getName()))
                .willReturn(updatedAl);

        Assert.assertEquals(expected, alignmentService.updateAlignment(request, user));
    }
}
