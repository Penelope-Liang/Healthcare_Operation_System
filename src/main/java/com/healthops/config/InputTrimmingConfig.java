package com.healthops.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * Every text value in a JSON body is trimmed, and blank input becomes {@code null}. Validation then only has to distinguish "missing" ({@code @NotBlank}) from
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
}
