package com.healthops.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

import java.io.IOException;

/**
 * Every text input is trimmed, and blank input becomes {@code null}, for both HTML forms and
 * JSON bodies. Validation then only has to distinguish "missing" ({@code @NotBlank}) from
 * "malformed" ({@code @Pattern}), and optional fields such as spouse OHIP stay {@code NULL}.
 */
@Configuration
public class InputTrimmingConfig {

    @Bean
    Jackson2ObjectMapperBuilderCustomizer trimJsonStrings() {
        SimpleModule module = new SimpleModule("trim-strings");
        module.addDeserializer(String.class, new StringDeserializer() {
            @Override
            public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                String value = super.deserialize(p, ctxt);
                if (value == null) {
                    return null;
                }
                String trimmed = value.trim();
                return trimmed.isEmpty() ? null : trimmed;
            }
        });
        return builder -> builder.modulesToInstall(module);
    }

    @ControllerAdvice
    static class TrimFormFields {
        @InitBinder
        void trimStrings(WebDataBinder binder) {
            binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
        }
    }
}
