package com.dna.application.backend.service;

import com.dna.application.backend.TestDataGenerator;
import com.dna.application.backend.dto.AlignmentDto;
import com.dna.application.backend.exception.EntityNameAlreadyExistsException;
import com.dna.application.backend.model.Alignment;
import com.dna.application.backend.model.AlignmentRequest;
import com.dna.application.backend.model.User;
import com.dna.application.backend.repository.AlignmentRepository;
import com.dna.application.backend.repository.BamUrlRepository;
import com.dna.application.backend.repository.ReferenceRepository;
import com.dna.application.backend.repository.UserRepository;
import com.dna.application.backend.util.BaseCommandRunner;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.util.StopWatch;

import java.util.Optional;

import static org.mockito.BDDMockito.given;

@Slf4j
@RunWith(MockitoJUnitRunner.class)
public class AlignerServiceTest extends BaseCommandRunner {
    @Mock
    private UserRepository userRepository;

    @Mock
    private AlignmentRepository alignmentRepository;

    @Mock
    private ReferenceRepository referenceRepository;

    @Mock
    private BamUrlRepository bamUrlRepository;

    @Mock
    private AlignmentService alignmentService;

    @InjectMocks
    private BowtieService bowtieService;

    @InjectMocks
    private BwaService bwaService;

    @InjectMocks
    private SnapService snapService;

    private TestDataGenerator testDataGenerator = new TestDataGenerator();
    private StopWatch watch = new StopWatch();

    private String testFolder = "src/test/resources/files/";

    private User user = testDataGenerator.getAdmin();
    private User guest = testDataGenerator.getGuest();
    private AlignmentRequest singleRequest = testDataGenerator.getNewSingleAlignmentRequest();
    private AlignmentRequest pairedRequest = testDataGenerator.getNewPairedAlignmentRequest();
    private AlignmentDto newAlignmentDto = testDataGenerator.getNewAlignmentDto();

    @Test(expected = EntityNameAlreadyExistsException.class)
    public void align_NameExists_Exception() throws Exception {
        given(alignmentRepository.existsByName("test"))
                .willReturn(true);
        bowtieService.align(singleRequest, user);
    }

    @Test
    public void align_BowtieSingleNoRefId_ReturnDto() throws Exception {
        bowtieService.setFolderForTest(testFolder);
        given(alignmentRepository.existsByName("test"))
                .willReturn(false);
        given(userRepository.findByUsername("guest"))
                .willReturn(guest);
        given(alignmentService.getAlignmentDto(singleRequest.getName()))
                .willReturn(newAlignmentDto);
        watch.start();
        Assert.assertEquals(newAlignmentDto, bowtieService.align(singleRequest, user));
        watch.stop();
        log.info("Bowtie Single no ref: " + watch.getLastTaskTimeMillis());
        afterAlignmentFileCheck(false);
    }

    @Test
    public void align_BowtiePairedRefId_ReturnDto() throws Exception {
        pairedRequest.setReferenceId(1L);
        bowtieService.setFolderForTest(testFolder);
        given(alignmentRepository.existsByName("test"))
                .willReturn(false);
        given(referenceRepository.findById(pairedRequest.getReferenceId()))
                .willReturn(Optional.of(testDataGenerator.getReferenceExample()));
        given(userRepository.findByUsername("guest"))
                .willReturn(guest);
        given(alignmentService.getAlignmentDto(pairedRequest.getName()))
                .willReturn(newAlignmentDto);
        watch.start();
        Assert.assertEquals(newAlignmentDto, bowtieService.align(pairedRequest, user));
        watch.stop();
        log.info("Bowtie Paired with ref: " + watch.getLastTaskTimeMillis());
        afterAlignmentFileCheck(true);
    }

    @Test
    public void align_BwaSingleNoRefId_ReturnDto() throws Exception {
        singleRequest.setAligner(Alignment.Aligner.BWA);
        newAlignmentDto.setAligner(Alignment.Aligner.BWA);
        bwaService.setFolderForTest(testFolder);
        given(alignmentRepository.existsByName("test"))
                .willReturn(false);
        given(userRepository.findByUsername("guest"))
                .willReturn(guest);
        given(alignmentService.getAlignmentDto(singleRequest.getName()))
                .willReturn(newAlignmentDto);
        watch.start();
        Assert.assertEquals(newAlignmentDto, bwaService.align(singleRequest, user));
        watch.stop();
        log.info("Bwa Single no ref: " + watch.getLastTaskTimeMillis());
        afterAlignmentFileCheck(false);
    }

    @Test
    public void align_BwaPairedRefId_ReturnDto() throws Exception {
        pairedRequest.setReferenceId(1L);
        pairedRequest.setAligner(Alignment.Aligner.BWA);
        newAlignmentDto.setAligner(Alignment.Aligner.BWA);
        bwaService.setFolderForTest(testFolder);
        given(alignmentRepository.existsByName("test"))
                .willReturn(false);
        given(referenceRepository.findById(pairedRequest.getReferenceId()))
                .willReturn(Optional.of(testDataGenerator.getReferenceExample()));
        given(userRepository.findByUsername("guest"))
                .willReturn(guest);
        given(alignmentService.getAlignmentDto(pairedRequest.getName()))
                .willReturn(newAlignmentDto);
        watch.start();
        Assert.assertEquals(newAlignmentDto, bwaService.align(pairedRequest, user));
        watch.stop();
        log.info("Bwa Paired with ref: " + watch.getLastTaskTimeMillis());
        afterAlignmentFileCheck(true);
    }

    @Test
    public void align_SnapSingleNoRefId_ReturnDto() throws Exception {
        singleRequest.setAligner(Alignment.Aligner.SNAP);
        newAlignmentDto.setAligner(Alignment.Aligner.SNAP);
        snapService.setFolderForTest(testFolder);
        given(alignmentRepository.existsByName("test"))
                .willReturn(false);
        given(userRepository.findByUsername("guest"))
                .willReturn(guest);
        given(alignmentService.getAlignmentDto(singleRequest.getName()))
                .willReturn(newAlignmentDto);
        watch.start();
        Assert.assertEquals(newAlignmentDto, snapService.align(singleRequest, user));
        watch.stop();
        log.info("Snap single no ref: " + watch.getLastTaskTimeMillis());
        afterAlignmentFileCheck(false);
    }

    @Test
    public void align_SnapPairedRefId_ReturnDto() throws Exception {
        pairedRequest.setReferenceId(1L);
        pairedRequest.setAligner(Alignment.Aligner.SNAP);
        newAlignmentDto.setAligner(Alignment.Aligner.SNAP);
        snapService.setFolderForTest(testFolder);
        given(alignmentRepository.existsByName("test"))
                .willReturn(false);
        given(referenceRepository.findById(pairedRequest.getReferenceId()))
                .willReturn(Optional.of(testDataGenerator.getReferenceExample()));
        given(userRepository.findByUsername("guest"))
                .willReturn(guest);
        given(alignmentService.getAlignmentDto(pairedRequest.getName()))
                .willReturn(newAlignmentDto);
        watch.start();
        Assert.assertEquals(newAlignmentDto, snapService.align(pairedRequest, user));
        watch.stop();
        log.info("Snap paired with ref: " + watch.getLastTaskTimeMillis());
        afterAlignmentFileCheck(true);
    }

    private void afterAlignmentFileCheck(boolean isRef) throws Exception {
        String filesExistMessage;
        if(isRef)
            filesExistMessage = runCommand(new String[]{"ls", "-la", testFolder+"/bams/test1.bam", testFolder+"/bams/test1.bam.bai",});
        else
            filesExistMessage = runCommand(new String[]{"ls", "-la", testFolder+"/bams/test1.bam", testFolder+"/bams/test1.bam.bai",
                testFolder+"/references/test.fna", testFolder+"/references/test.fna.fai"});

        Assert.assertFalse(filesExistMessage.toLowerCase().contains("no such file or directory"));

        String deletedReads = runCommand(new String[]{"ls", "-la", testFolder+"reads.fq"});
        Assert.assertTrue(deletedReads.toLowerCase().contains("no such file or directory"));
    }
}
