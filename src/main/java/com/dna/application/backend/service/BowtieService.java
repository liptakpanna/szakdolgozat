package com.dna.application.backend.service;

import com.dna.application.backend.model.ReadTrack;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class BowtieService extends AbstractAligner {

    @Override
    protected List<String> doAlignmentOnTrack(ReadTrack track, String filename, String indexName) throws Exception {
        List<String> args = new ArrayList<>();
        MultipartFile readFile = track.getRead1();
        String extension = FilenameUtils.getExtension(readFile.getOriginalFilename());
        String read1 = saveFile(readFile, folder + filename + "1" + "." + extension);
        String read2 = "";
        if(track.getValidCount().equals("all"))
            args.addAll(Arrays.asList("bowtie", "-a"));
        else if(track.getValidCount().equals("all --best"))
            args.addAll(Arrays.asList("bowtie", "-a", "--best"));
        else
            args.addAll(Arrays.asList("bowtie", "-k", track.getValidCount()));
        args.addAll(Arrays.asList("-v", track.getMismatch()));
        if (track.isPaired()) {
            MultipartFile readFile2 = track.getRead1();
            read2 = saveFile(readFile2, folder + filename + "2" + "." + extension);
            if(fastaExtensions.contains(extension))
                args.addAll(Arrays.asList("-S", indexName, "-f", "-1", read1, "-2", read2, folder + "bams/" + filename + ".sam"));
            else
                args.addAll(Arrays.asList("-S", indexName, "-1", read1, "-2", read2, folder + "bams/" + filename + ".sam"));
        } else {
            if(fastaExtensions.contains(extension))
                args.addAll(Arrays.asList("-S", indexName, "-f", read1, folder+"bams/"+ filename + ".sam"));
            else
                args.addAll(Arrays.asList("-S", indexName, read1, folder+"bams/"+ filename + ".sam"));
        }
        runCommand(args.toArray(new String[0]));

        return Arrays.asList(read1, read2);
    }

    @Override
    protected String doIndex(boolean isExample, String filename) throws Exception{
        if(isExample) return folder+"examples/indexes/"+filename;
        else {
            runCommand(new String[]{"bowtie-build", folder+"references/"+filename+".fna", folder+filename});
            return folder+"/"+filename;
        }
    }

    @Override
    protected void deleteIndex(String filename) throws Exception {
        runCommand(new String[]{"rm", folder + filename + ".*.ebwt"});
    }
}
