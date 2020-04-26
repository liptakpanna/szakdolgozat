package com.dna.application.backend.service;

import com.dna.application.backend.dto.AlignmentDto;
import com.dna.application.backend.exception.CommandNotFoundException;
import com.dna.application.backend.exception.EntityNameAlreadyExistsException;
import com.dna.application.backend.exception.WrongFileTypeException;
import com.dna.application.backend.model.*;
import com.dna.application.backend.repository.AlignmentRepository;
import com.dna.application.backend.repository.BamUrlRepository;
import com.dna.application.backend.repository.ReferenceRepository;
import com.dna.application.backend.repository.UserRepository;
import com.dna.application.backend.util.BaseCommandRunner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Slf4j
@Service
public abstract class AbstractAligner extends BaseCommandRunner {
    @Autowired
    private AlignmentRepository alignmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReferenceRepository referenceRepository;

    @Autowired
    private BamUrlRepository bamUrlRepository;

    @Autowired
    private AlignmentService alignmentService;

    @Value("${data.resource.folder}")
    public String folder;

    @Value("${data.resource.url}")
    public String resourceUrl;

    final static List<String> fastaExtensions = Arrays.asList("fna", "fa", "fasta");

    final static List<String> fileErrorMessages = Arrays.asList("Unknown file type","FASTA file doesn't beging with a contig name",
            "Reference file does not seem to be a FASTA file",
            "reads file does not look like");

    protected abstract List<String> doAlignmentOnTrack(ReadTrack track, String filename, String indexName) throws Exception;

    protected abstract String getIndex(boolean isExample, String filename) throws Exception;

    protected abstract void deleteIndex(String filename) throws Exception;

    @Transactional
    public AlignmentDto align(AlignmentRequest alignmentRequest, User user) throws Exception{
        String name = alignmentRequest.getName();
        if(alignmentRepository.existsByName(name)) throw new EntityNameAlreadyExistsException();
        String filename = name.replaceAll("\\s+","_");

        List<String> usernameAccessList = alignmentRequest.getUsernameAccessList();
        List<ReadTrack> readTracks = alignmentRequest.getReadsForDna();
        Long referenceId = alignmentRequest.getReferenceId();
        ReferenceExample reference = null;
        String indexFile;

        if(referenceId != null){
            Optional<ReferenceExample> optionalReference = referenceRepository.findById(referenceId);
            if(optionalReference.isEmpty()) throw new EntityNotFoundException(referenceId.toString());
            reference = optionalReference.get();
            indexFile = getIndex(true, reference.getFilename());
        }
        else {
            saveFile(alignmentRequest.getReferenceDna(), folder+"references/"+filename+".fna");
            indexFile = getIndex(false, filename);
        }

        doAlignmentOnTracks(readTracks, filename, indexFile, referenceId, alignmentRequest.getAligner() == Alignment.Aligner.SNAP);

        Alignment alignment = Alignment.builder()
                .aligner(alignmentRequest.getAligner())
                .name(name)
                .description(alignmentRequest.getDescription())
                .owner(user)
                .referenceUrl(reference==null ? resourceUrl+"/references/"+filename+".fna" : resourceUrl+"/examples/"+reference.getFilename()+".fna" )
                .bamUrls(getBamUrls(filename, readTracks))
                .visibility(alignmentRequest.getVisibility())
                .build();

        Set<User> userAccess = setUserAccessSet(usernameAccessList, alignment);
        alignment.setUserAccess(userAccess);
        alignmentRepository.saveAndFlush(alignment);

        return alignmentService.getAlignmentDto(name);
    }

    static String saveFile(MultipartFile multipartFile, String filename) {
        try {
            byte[] bytes = multipartFile.getBytes();
            Path path = Paths.get(filename);
            Files.write(path, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filename;
    }

    @Transactional
    private Set<User> setUserAccessSet(List<String> usernameAccessList, Alignment alignment){
        Set<User> userAccess = new HashSet<>();
        if(usernameAccessList != null)
            for(String username: usernameAccessList) {
                User userToAdd = userRepository.findByUsername(username);
                if(userToAdd != null){
                    userToAdd.getAlignmentAccess().add(alignment);
                    userAccess.add(userToAdd);
                }
            }
        return userAccess;
    }

    protected void runAlignCommand(String[] args) throws Exception {
        Process proc = new ProcessBuilder(args).start();
        String input = getInput(proc);
        String error = getError(proc);
        if(fileErrorMessages.parallelStream().anyMatch(error::contains) || fileErrorMessages.parallelStream().anyMatch(input::contains) )
            throw new WrongFileTypeException();
        if(error.toLowerCase().contains("command not found") || input.toLowerCase().contains("command not found"))
            throw new CommandNotFoundException();

        proc.waitFor();
        log.debug(input+error);
    }

    @Transactional
    private Set<BamUrl> getBamUrls(String filename, List<ReadTrack> tracks){
        Set<BamUrl> bamUrls = new HashSet<>();
        for(int i = 0; i < tracks.size(); i++){
            bamUrls.add(new BamUrl(tracks.get(i).getName(),resourceUrl+"/bams/"+filename+(i+1)+".bam"));
        }
        bamUrlRepository.saveAll(bamUrls);
        bamUrlRepository.flush();
        return bamUrls;
    }

    private void deleteReadFiles(List<String> readFiles) throws Exception{
        for(String readFile : readFiles) {
            if(readFile != null && !readFile.equals(""))
                runCommand(new String[]{"rm", readFile});
        }
    }

    private void doAlignmentOnTracks(List<ReadTrack> readTracks, String filename, String indexFile, Long referenceId, boolean isSnap) throws Exception{
        int trackCount=1;
        if(readTracks == null) throw new Exception("No reads.");
        for(ReadTrack track : readTracks) {
            List<String> readNames = doAlignmentOnTrack(track, filename, indexFile);
            runCommand(new String[]{folder+ "sam_to_bam_script",filename, folder, String.valueOf(trackCount), String.valueOf(referenceId != null)});
            trackCount++;
            deleteReadFiles(readNames);
        }
        if (referenceId == null || isSnap)
            deleteIndex(filename);
    }

    public void setFolderForTest(String testFolder){folder = testFolder;}
}
