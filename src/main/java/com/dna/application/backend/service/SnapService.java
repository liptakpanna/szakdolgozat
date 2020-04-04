package com.dna.application.backend.service;

import com.dna.application.backend.model.ReadTrack;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class SnapService extends AbstractAligner {


    @Override
    protected List<String> doAlignmentOnTrack(ReadTrack track, String filename, String indexName) throws Exception {
        List<String> args = new ArrayList<>();
        MultipartFile readFile = track.getRead1();
        String extension = FilenameUtils.getExtension(readFile.getOriginalFilename());
        String read1 = saveFile(readFile, folder + filename + "1" + "." + extension);
        String read2 = "";
        if (track.isPaired()) {
            MultipartFile readFile2 = track.getRead1();
            read2 = saveFile(readFile2, folder + filename + "2" + "." + extension);
            args.addAll(Arrays.asList("snap-aligner", "paired", folder, read1, read2,
                    "-H", track.getMaxHits(),
                    "-d", track.getMaxDist(),
                    "-o", folder + "bams/" + filename + ".sam"));
        } else {
            args.addAll(Arrays.asList("snap-aligner", "single", folder, read1,
                    "-h", track.getMaxHits(),
                    "-d", track.getMaxDist(),
                    "-o", folder + "bams/" + filename + ".sam"));
        }
        runCommand(args.toArray(new String[0]));

        return Arrays.asList(read1, read2);
    }

    @Override
    protected String doIndex(boolean isExample, String filename) throws Exception {
        if(isExample){
            runCommand(new String[]{"snap-aligner", "index",folder+"examples/"+filename+".fna", folder, "-bSpace"});
        }
        else {
            runCommand(new String[]{"snap-aligner", "index", folder+"references/"+filename+".fna", folder, "-bSpace"});
        }
        return folder;
    }

    @Override
    protected void deleteIndex(String filename) throws Exception {
        runCommand(new String[]{"rm", folder + "Genome"});
        runCommand(new String[]{"rm", folder + "GenomeIndex"});
        runCommand(new String[]{"rm", folder + "GenomeIndexHash"});
        runCommand(new String[]{"rm", folder + "OverflowTable"});
    }
}
