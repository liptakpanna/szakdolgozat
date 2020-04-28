package com.dna.application.backend.util;

import com.dna.application.backend.exception.CommandNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class RequirementCheckUtil extends BaseCommandRunner {

    final static List<String> commands = Arrays.asList("awk", "samtools", "bowtie", "bwa", "snap-aligner", "rm", "find", "mv");

    @EventListener(ApplicationReadyEvent.class)
    public void checkIfRequirementsAreMet() throws Exception {
        for (String command : commands) {
            runCommand(command.split(" "));
        }
    }

}
