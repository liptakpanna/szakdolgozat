package com.dna.application.backend.service;

import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class AlignerService {
    @Value("${data.resource.folder}")
    public String resourceFolder;

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
}
