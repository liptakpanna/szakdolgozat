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
public class BwaService extends AbstractAligner {

    @Override
    protected List<String> doAlignmentOnTrack(ReadTrack read, String filename, String indexName) throws Exception {
        List<String> args = new ArrayList<>();
        MultipartFile readFile = read.getRead1();
        String extension = FilenameUtils.getExtension(readFile.getOriginalFilename());
        String read1 = saveFile(readFile, folder + filename + "1" + "." + extension);
        String read2 = "";
        if (read.isPaired()) {
            MultipartFile readFile2 = read.getRead1();
            read2 = saveFile(readFile2, folder + filename + "2" + "." + extension);
        }
        args.addAll(Arrays.asList(folder+"bwa_script", filename, folder, indexName));
        args.addAll(read.getPenalties());
        args.addAll(Arrays.asList(String.valueOf(read.isPaired()), read1, read2));
        runCommand(args.toArray(new String[0]));

        return Arrays.asList(read1, read2);
    }

    @Override
    protected String doIndex(boolean isExample, String filename) throws Exception {
        if(isExample) return folder+"/examples/"+filename+".fna";
        else {
            runCommand(new String[]{"bwa", "index", folder+"references/"+filename+".fna"});
            return folder+"references/"+filename+".fna";
        }
    }
}
