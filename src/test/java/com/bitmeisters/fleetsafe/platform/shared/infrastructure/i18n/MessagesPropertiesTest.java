package com.bitmeisters.fleetsafe.platform.shared.infrastructure.i18n;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

class MessagesPropertiesTest {

    private static Properties load(String file) throws IOException {
        var properties = new Properties();
        try (var reader = new InputStreamReader(
                MessagesPropertiesTest.class.getResourceAsStream("/" + file), StandardCharsets.UTF_8)) {
            properties.load(reader);
        }
        return properties;
    }

    @Test
    void englishAndSpanishDefineTheSameKeys() throws IOException {
        var english = load("messages.properties");
        var spanish = load("messages_es_419.properties");

        assertThat(spanish.stringPropertyNames())
                .as("keys in messages.properties missing from messages_es_419.properties, or the other way round")
                .containsExactlyInAnyOrderElementsOf(english.stringPropertyNames());
    }

    @Test
    void noMessageIsEmpty() throws IOException {
        for (var file : new String[] {"messages.properties", "messages_es_419.properties"}) {
            var properties = load(file);
            assertThat(properties.stringPropertyNames())
                    .as("empty messages in " + file)
                    .allSatisfy(key -> assertThat(properties.getProperty(key)).isNotBlank());
        }
    }
}
