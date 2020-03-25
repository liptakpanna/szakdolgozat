package com.dna.application.backend.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class EntityNameAlreadyExistsException extends Exception {
    public EntityNameAlreadyExistsException(String errorMessage) {
        super(errorMessage);
    }
}
