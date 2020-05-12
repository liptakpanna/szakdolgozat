package com.dna.application.backend.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CommandNotFoundException extends Exception {
    public CommandNotFoundException(Throwable e) {
        super(e);
    }
}
