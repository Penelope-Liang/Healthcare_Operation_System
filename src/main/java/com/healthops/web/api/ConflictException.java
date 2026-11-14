package com.healthops.web.api;

/** A write the database rejected: duplicate key, unknown foreign key, or a still-referenced row. */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
