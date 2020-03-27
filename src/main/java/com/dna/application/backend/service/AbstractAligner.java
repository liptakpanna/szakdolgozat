package com.dna.application.backend.service;

import com.dna.application.backend.dto.AlignmentDto;
import com.dna.application.backend.exception.EntityNameAlreadyExistsException;
import com.dna.application.backend.model.*;
import com.dna.application.backend.repository.AlignmentRepository;
import com.dna.application.backend.repository.BamUrlRepository;
import com.dna.application.backend.repository.ReferenceRepository;
import com.dna.application.backend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public abstract class AbstractAligner {
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

    @Value("${static.resource.url}")
    public String resourceUrl;

    final static List<String> fastaExtensions = Arrays.asList("fna", "fa", "fasta");

    protected abstract List<String> doAlignmentOnTrack(ReadTrack track, String filename, String indexName) throws Exception;

    protected abstract String doIndex(boolean isExample, String filename) throws Exception;

    protected abstract void deleteIndex(String filename) throws Exception;

    @Transactional
    public AlignmentDto align(AlignmentRequest alignmentRequest, User user) throws Exception{
        String name = alignmentRequest.getName();
        if(alignmentRepository.findByName(name) != null ) throw new EntityNameAlreadyExistsException();

        List<String> usernameAccessList = alignmentRequest.getUsernameAccessList();
        String filename = name.replaceAll("\\s+","_");
        List<ReadTrack> readTracks = alignmentRequest.getReadsForDna();

        Long referenceId = alignmentRequest.getReferenceId();
        ReferenceExample reference = null;
        String indexFile;
        if(referenceId != null){
            reference = referenceRepository.findById(referenceId).orElseThrow(() -> new EntityNotFoundException(referenceId.toString()));
            indexFile = doIndex(true, reference.getFilename());
        }
        else {
            saveFile(alignmentRequest.getReferenceDna(), folder+"references/"+filename+".fna");
            indexFile = doIndex(false, filename);
        }

        doAlignmentOnTracks(readTracks, filename, indexFile, referenceId);

        Alignment alignment = Alignment.builder()
                .aligner(alignmentRequest.getAligner())
                .name(name)
                .description(alignmentRequest.getDescription())
                .owner(user)
                .referenceUrl(reference==null ? resourceUrl+"/references/"+filename+".fna" : resourceUrl+"/examples/"+reference.getFilename()+".fna" )
                .bamUrls(getBamUrls(filename, readTracks))
                .visibility(alignmentRequest.getVisibility())
                .build();

        setUserAccessSet(usernameAccessList, alignment);
        alignmentRepository.saveAndFlush(alignment);

        return alignmentService.getAlignmentDto(name);
    }

    static String getInput(Process proc) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        StringBuilder ans = new StringBuilder();
        String line;

        while((line = reader.readLine()) != null) {
            ans.append(line);
        }
        return ans.toString();
    }

    static String getError(Process proc) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
        StringBuilder ans = new StringBuilder();
        String line;

        while((line = reader.readLine()) != null) {
            ans.append(line);
        }
        return ans.toString();
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

    private void setUserAccessSet(List<String> usernameAccessList, Alignment alignment){
        if(usernameAccessList != null)
            for(String username: usernameAccessList) {
                User userToAdd = userRepository.findByUsername(username);
                if(userToAdd != null){
                    userToAdd.getAlignmentAccess().add(alignment);
                }
            }
    }

    static void runCommand(String[] args) throws Exception {
        Process proc = new ProcessBuilder(args).start();
        String ans = getInput(proc);
        String error = getError(proc);

        proc.waitFor();
        log.warn(ans+error);
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

    private void doAlignmentOnTracks(List<ReadTrack> readTracks, String filename, String indexFile, Long referenceId) throws Exception{
        int trackCount=1;
        if(readTracks == null) throw new Exception("No reads.");
        for(ReadTrack track : readTracks) {
            List<String> readNames = doAlignmentOnTrack(track, filename, indexFile);
            runCommand(new String[]{folder+ "sam_to_bam_script",filename, folder, String.valueOf(trackCount), String.valueOf(referenceId != null)});
            trackCount++;
            deleteReadFiles(readNames);
        }
        deleteIndex(filename);
    }
}
