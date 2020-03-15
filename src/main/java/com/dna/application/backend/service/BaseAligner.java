package com.dna.application.backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

    static void runScript(String scriptName, String filename, String folder) throws Exception {
        String[] args = new String[]{scriptName, filename, folder};

        Process proc = new ProcessBuilder(args).start();
        String ans = getInput(proc);
        String error = getError(proc);

        proc.waitFor();
        log.warn(ans+error);
    }
}
