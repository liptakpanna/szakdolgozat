package com.dna.application.backend.service;

import com.dna.application.backend.dto.AlignmentDto;
import com.dna.application.backend.model.*;
import com.dna.application.backend.repository.AlignmentRepository;
import com.dna.application.backend.repository.ReferenceRepository;
import com.dna.application.backend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class AlignerService extends BaseAligner {
    @Autowired
    private AlignmentRepository alignmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReferenceRepository referenceRepository;

    @Autowired
    private AlignmentService alignmentService;

    final private List<String> fastaExtensions = Arrays.asList("fna", "fa", "fasta");

    @Transactional
    public AlignmentDto align(AlignmentRequest alignmentRequest, User user) throws Exception{
        String name = alignmentRequest.getName();
        if(alignmentRepository.findByName(name) != null ) throw new Exception("Name already in use");

        List<String> usernameAccessList = alignmentRequest.getUsernameAccessList();
        String filename = name.replaceAll("\\s+","_");
        String alignerName = alignmentRequest.getAligner().name();
        List<ReadTrack> readTracks = alignmentRequest.getReadsForDna();

        Long referenceId = alignmentRequest.getReferenceId();
        ReferenceExample reference = null;
        if(referenceId != null){
            reference = referenceRepository.findById(referenceId).orElseThrow(() -> new EntityNotFoundException(referenceId.toString()));
            String indexFile = folder+"/examples/"+reference.getFilename();
            doAlignmentOnTracks(readTracks, filename, indexFile, true, alignerName);
        }
        else {
            saveFile(alignmentRequest.getReferenceDna(), folder+"references/"+filename+".fna");
            runScript(folder+"/"+ alignerName + "_index_script", filename, folder);
            String indexFile = folder+filename;

            doAlignmentOnTracks(readTracks, filename, indexFile, false, alignerName);
        }

        Alignment alignment = Alignment.builder()
                .aligner(Alignment.Aligner.BOWTIE)
                .name(filename)
                .description(alignmentRequest.getDescription())
                .owner(user)
                .referenceUrl(reference==null ? resourceUrl+"/references/"+filename+".fna" : resourceUrl+"/examples/"+reference.getFilename()+".fna" )
                .bamUrls(getBamUrls(resourceUrl, filename, readTracks))
                .visibility(alignmentRequest.getVisibility())
                .build();

        setUserAccessSet(usernameAccessList, userRepository, alignment);
        alignmentRepository.saveAndFlush(alignment);

        return alignmentService.getAlignmentDto(name);
    }

    private void doAlignmentOnTracks(List<ReadTrack> reads, String filename, String indexName, boolean isExample, String alignerName) throws Exception{
        int fileNumber = 1;
        int trackNumber = 1;
        for( ReadTrack read : reads) {
            MultipartFile readFile = read.getRead1();
            String extension = FilenameUtils.getExtension(readFile.getOriginalFilename());
            String read1 = saveFile(readFile, folder + filename + fileNumber + "." + extension);
            fileNumber++;
            if (read.isPaired()) {
                MultipartFile readFile2 = read.getRead1();
                String read2 = saveFile(readFile2, folder + filename + fileNumber + "." + extension);
                runScript(folder+"/" + alignerName +"_script", filename, folder, indexName, trackNumber, isExample, fastaExtensions.contains(extension),true, read1, read2);
                fileNumber++;
            }
            else{
                runScript(folder+"/" + alignerName +"_script", filename, folder, indexName, trackNumber,isExample, fastaExtensions.contains(extension),false, read1, "");
            }
            trackNumber++;
        }
    }
}
