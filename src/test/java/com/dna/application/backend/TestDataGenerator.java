package com.dna.application.backend;

import com.dna.application.backend.dto.AlignmentDto;
import com.dna.application.backend.dto.UserDto;
import com.dna.application.backend.model.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Slf4j
@Getter
public class TestDataGenerator {
    private String testFolder = "src/test/resources/files/";

    private Date timestamp = new Date();
    private Date timestamp2 = new Date();

    private User admin = new User(1L, "admin","admin@email.com", User.Role.ADMIN, timestamp, timestamp2,User.Status.ENABLED, "[initial]", "updater", new HashSet<>(), new HashSet<>());
    private UserDto adminDto = new UserDto( 1L,"admin","admin@email.com", User.Role.ADMIN, "updater", timestamp, timestamp2,"[initial]" );
    private User guest = new User(2L, "guest","guest@email.com", User.Role.GUEST, timestamp2, timestamp,User.Status.ENABLED, "creator", "updater", new HashSet<>(), new HashSet<>());
    private UserDto guestDto = new UserDto( 2L,"guest","guest@email.com", User.Role.GUEST, "updater", timestamp, timestamp2,"creator" );
    private User researcher = new User(3L, "test","test@test.com", User.Role.RESEARCHER, timestamp2, timestamp,User.Status.ENABLED, "admin", "admin2", new HashSet<>(), new HashSet<>());
    private UserDto researcherDto = new UserDto( 3L,"test","test@test.com", User.Role.RESEARCHER, "admin2", timestamp2, timestamp,"admin" );
    private User researcherDeleted = new User(3L, "test","test@test.com", User.Role.RESEARCHER, timestamp2, timestamp,User.Status.DELETED, "admin", "admin2", new HashSet<>(), new HashSet<>());
    private User updatedUser = new User(2L, "newname", "new@email.com", "newpwd", User.Role.RESEARCHER, "updater");

    private UserRequest userRequest = new UserRequest(2L, "newname", "new@email.com", "newpwd", User.Role.RESEARCHER);

    private AlignmentRequest alignmentRequest = new AlignmentRequest(1L, "b", "New Description", Alignment.Visibility.PRIVATE);

    private BamUrl bam = new BamUrl("a1", "localhost:9090/resources/bams/a1.bam");
    private BamUrl bamUpdated = new BamUrl("track", "localhost:9090/resources/bams/b1.bam");

    private Alignment publicAlignmentDeletedOwner = new Alignment(1L,"a", "localhost:9090/resources/files/references/a.fna", new HashSet<>(Collections.singletonList(bam)), "Lorem ipsum", Alignment.Aligner.BOWTIE ,Alignment.Visibility.PUBLIC, researcherDeleted, timestamp, timestamp, "updater", null);
    private AlignmentDto publicAlignmentDtoDeletedOwner = new AlignmentDto(1L,"a", Alignment.Aligner.BOWTIE, "Lorem ipsum", "localhost:9090/resources/files/references/a.fna", new HashSet<>(Collections.singletonList(bam)) ,Alignment.Visibility.PUBLIC, "[deleted user]", timestamp, timestamp, "updater", new ArrayList<>());
    private Alignment privateAlignment = new Alignment(1L,"a", "localhost:9090/resources/examples/test", new HashSet<>(Collections.singletonList(bam)), "Lorem ipsum", Alignment.Aligner.BOWTIE ,Alignment.Visibility.PRIVATE, researcher, timestamp, timestamp, "updater", null);
    private AlignmentDto privateAlignmentDto = new AlignmentDto(1L,"a", Alignment.Aligner.BOWTIE, "Lorem ipsum", "localhost:9090/resources/examples/test", new HashSet<>(Collections.singletonList(bam)) ,Alignment.Visibility.PRIVATE, researcher.getUsername(), timestamp, timestamp, "updater", new ArrayList<>());
    private Alignment privateGroupAlignment = new Alignment(1L,"a", "localhost:9090/resources/examples/test", null, "Lorem ipsum", Alignment.Aligner.BOWTIE ,Alignment.Visibility.PRIVATE_GROUP, researcher, timestamp, timestamp, "updater", null);
    private AlignmentDto privateGroupAlignmentDto = new AlignmentDto(1L,"a", Alignment.Aligner.BOWTIE, "Lorem ipsum", "localhost:9090/resources/examples/test", null ,Alignment.Visibility.PRIVATE_GROUP, researcher.getUsername(), timestamp, timestamp, "updater", new ArrayList<>());
    private Alignment updatedAlignment = new Alignment(1L,"b", "localhost:9090/resources/references/b.fna", new HashSet<>(Collections.singletonList(bamUpdated)), "New Description", Alignment.Aligner.BOWTIE ,Alignment.Visibility.PRIVATE, researcherDeleted, timestamp, timestamp, "admin", null);
    private AlignmentDto updatedAlignmentDto = new AlignmentDto(1L,"b", Alignment.Aligner.BOWTIE, "New Description", "localhost:9090/resources/references/b.fna", new HashSet<>(Collections.singletonList(bamUpdated)) ,Alignment.Visibility.PRIVATE, "[deleted user]", timestamp, timestamp, "admin", new ArrayList<>());

    private ReadTrack singleTrack = new ReadTrack("first", false, getMultipartFileFromFile("reads/reads.fastq"), null, "all", "2", Arrays.asList("3", "11", "4"), "100", "10");
    private ReadTrack pairedTrack = new ReadTrack("first", true, getMultipartFileFromFile("reads/reads.fastq"), getMultipartFileFromFile("reads/reads2.fastq"), "all", "2", Arrays.asList("3", "11", "4"), "100", "10");

    private AlignmentRequest newSingleAlignmentRequest = new AlignmentRequest(null, Alignment.Aligner.BOWTIE, "test", "Lorem Ipsum", getMultipartFileFromFile("ecoli.fna"), Collections.singletonList(singleTrack), Alignment.Visibility.PRIVATE, Collections.singletonList("guest"), null);
    private AlignmentRequest newPairedAlignmentRequest = new AlignmentRequest(null, Alignment.Aligner.BOWTIE, "test", "Lorem Ipsum", getMultipartFileFromFile("ecoli.fna"), Arrays.asList(singleTrack, pairedTrack), Alignment.Visibility.PRIVATE, Collections.singletonList("guest"), null);

    private BamUrl newBam = new BamUrl("first", "localhost:9090/resources/bams/test1.bam");

    private Alignment newAlignment = new Alignment(1L,"test", "localhost:9090/resources/files/references/test.fna", new HashSet<>(Collections.singletonList(newBam)), "Lorem Ipsum", Alignment.Aligner.BOWTIE ,Alignment.Visibility.PRIVATE, admin, timestamp, timestamp, null, null);
    private AlignmentDto newAlignmentDto = new AlignmentDto(1L,"test", Alignment.Aligner.BOWTIE, "Lorem Ipsum", "localhost:9090/resources/files/references/test.fna", new HashSet<>(Collections.singletonList(newBam)) ,Alignment.Visibility.PRIVATE, admin.getUsername(), timestamp, timestamp, null, Collections.singletonList(guest.getUsername()));

    private ReferenceExample referenceExample = new ReferenceExample("ecoli", "description", "ecoli");

    public MultipartFile getMultipartFileFromFile(String filename) {
        Path path = Paths.get(testFolder+ filename);
        String contentType = "text/plain";
        byte[] content = null;
        try {
            content = Files.readAllBytes(path);
        } catch (final IOException e) {
            log.warn(String.valueOf(e));
        }
        return new MockMultipartFile(filename,
                filename, contentType, content);
    }
}
