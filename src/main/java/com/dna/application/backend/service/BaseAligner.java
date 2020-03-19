package com.dna.application.backend.service;

import com.dna.application.backend.model.User;
import com.dna.application.backend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class BaseAligner {
    @Value("${data.resource.folder}")
    public String folder;

    @Value("${static.resource.url}")
    public String resourceUrl;

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

    static void saveFile(MultipartFile multipartFile, String filename) {
        try {
            byte[] bytes = multipartFile.getBytes();
            Path path = Paths.get(filename);
            Files.write(path, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void saveFilesForAligner(MultipartFile referenceDna, Set<MultipartFile> reads, String folder, String filename){
        if(referenceDna != null)
            saveFile(referenceDna, folder+"references/"+filename + ".fna");
        int index = 1;
        if (reads != null)
            for(MultipartFile read: reads){
                saveFile(read, folder+filename + index + ".fastq");
                index++;
            }
        else
            log.error("NO READS");
    }

    static Set<User> getUserAccessSet(List<String> usernameAccessList, UserRepository userRepository){
        Set<User> userAccessSet = new HashSet<>();

        if( usernameAccessList != null && !usernameAccessList.isEmpty()) {
            userAccessSet.addAll(userRepository.findByUsername(usernameAccessList));
        }

        return userAccessSet;
    }

    static void runScript(String scriptName, String filename, String folder, int readsSize) throws Exception {
        String[] args = new String[]{scriptName, filename, folder, String.valueOf(readsSize)};

        Process proc = new ProcessBuilder(args).start();
        String ans = getInput(proc);
        String error = getError(proc);

        proc.waitFor();
        log.warn(ans+error);
    }

    static void runScript(String scriptName, String filename, String folder, int readsSize, String existingReference) throws Exception {
        String[] args = new String[]{scriptName, filename, folder, String.valueOf(readsSize), existingReference};

        Process proc = new ProcessBuilder(args).start();
        String ans = getInput(proc);
        String error = getError(proc);

        proc.waitFor();
        log.warn(ans+error);
    }

    static Set<String> getBamUrls(String resourceUrl, String filename, int size){
        Set<String> bamUrls = new HashSet<>();
        for(int i = 1; i <= size; i++){
            bamUrls.add(resourceUrl+"/bams/"+filename+i+".bam");
        }
        return bamUrls;
    }
}
