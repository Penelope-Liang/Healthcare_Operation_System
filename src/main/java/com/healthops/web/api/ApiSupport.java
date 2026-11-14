package com.healthops.web.api;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

/** Helpers shared by the REST controllers. */
@Component
public class ApiSupport {

    private final Validator validator;

    public ApiSupport(Validator validator) {
        this.validator = validator;
    }

    /**
     * Validates a body after the controller has filled in fields taken from the URL
     * (e.g. the OHIP on {@code PUT /api/patients/{ohip}}).
     */
    public void validate(Object body) {
        Map<String, String> errors = new LinkedHashMap<>();
        validator.validate(body).stream()
                .sorted(Comparator.comparing((ConstraintViolation<Object> v) -> v.getPropertyPath().toString()))
                .forEach(v -> errors.putIfAbsent(v.getPropertyPath().toString(), v.getMessage()));
        if (!errors.isEmpty()) {
            throw new RequestValidationException(errors);
        }
    }

    /** Runs a write and turns database constraint failures into a readable 409. */
    public <T> T write(Supplier<T> action, String duplicateMessage, String linkedMessage) {
        try {
            return action.get();
        } catch (DuplicateKeyException e) {
            throw new ConflictException(duplicateMessage);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(linkedMessage);
        }
    }

    public void write(Runnable action, String duplicateMessage, String linkedMessage) {
        write(() -> {
            action.run();
            return null;
        }, duplicateMessage, linkedMessage);
    }
}
