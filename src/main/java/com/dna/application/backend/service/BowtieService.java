package com.dna.application.backend.service;

import com.dna.application.backend.model.User;
import com.dna.application.backend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Slf4j
@Service
public class BowtieService extends AlignerService {
    @Autowired
    private UserRepository userRepository;

    public String align(User user) throws Exception{
        String[] args = new String[]{"bowtie", resourceFolder + "/bowtie/indexes/e_coli", resourceFolder + "/bowtie/reads/e_coli_1000.fq"};

        Process proc = new ProcessBuilder(args).start();
        String ans = getInput(proc);
        String error = getError(proc);

        proc.waitFor();
        return ans + error;
    }
}
