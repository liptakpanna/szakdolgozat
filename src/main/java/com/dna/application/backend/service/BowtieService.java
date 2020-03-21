package com.dna.application.backend.service;

import com.dna.application.backend.model.ReadTrack;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Service
public class BowtieService extends AbstractAligner {

    @Override
    protected List<String> doAlignmentOnTrack(ReadTrack read, String filename, String indexName) throws Exception {
        List<String> args;
        MultipartFile readFile = read.getRead1();
        String extension = FilenameUtils.getExtension(readFile.getOriginalFilename());
        String read1 = saveFile(readFile, folder + filename + "1" + "." + extension);
        String read2 = null;
        if (read.isPaired()) {
            MultipartFile readFile2 = read.getRead1();
            read2 = saveFile(readFile2, folder + filename + "2" + "." + extension);
            if(fastaExtensions.contains(extension))
                args = Arrays.asList("bowtie", "-S", indexName, "-f", "-1", read1, "-2", read2, folder+"bams/"+ filename + ".sam");
            else
                args = Arrays.asList("bowtie", "-S", indexName, "-1", read1, "-2", read2, folder+"bams/"+ filename + ".sam");
        }
        else{
            if(fastaExtensions.contains(extension))
                args = Arrays.asList("bowtie", "-S", indexName, "-f", read1, folder+"bams/"+ filename + ".sam");
            else
                args = Arrays.asList("bowtie", "-S", indexName, read1, folder+"bams/"+ filename + ".sam");
        }
        runCommand(args.toArray(new String[0]));

        return Arrays.asList(read1, read2);
    }

    @Override
    protected String doIndex(boolean isExample, String filename) throws Exception{
        if(isExample) return folder+"/examples/indexes/"+filename;
        else {
            runCommand(new String[]{"bowtie-build", folder+"references/"+filename+".fna", folder+"/"+filename});
            return folder+"/"+filename;
        }
    }
}
