package com.dna.application.backend.util;

import com.dna.application.backend.exception.CommandNotFoundException;
import com.dna.application.backend.exception.WrongFileTypeException;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Slf4j
public abstract class BaseCommandRunner {
    protected String getInput(Process proc) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        StringBuilder ans = new StringBuilder();
        String line;

        while((line = reader.readLine()) != null) {
            ans.append(line);
        }
        return ans.toString();
    }

    protected String getError(Process proc) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
        StringBuilder ans = new StringBuilder();
        String line;

        while((line = reader.readLine()) != null) {
            ans.append(line);
        }
        return ans.toString();
    }

    public String runCommand(String[] args) throws Exception {
        try {
            Process proc = new ProcessBuilder(args).start();
            String input = getInput(proc);
            String error = getError(proc);

            proc.waitFor();
            return input+error;
        } catch(IOException e) {
            if(String.valueOf(e).toLowerCase().contains("cannot run")) {
                throw new CommandNotFoundException(e);
            }
        }
        return null;
    }
}
