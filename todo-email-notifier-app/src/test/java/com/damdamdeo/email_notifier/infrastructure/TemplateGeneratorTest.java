package com.damdamdeo.email_notifier.infrastructure;

import com.damdamdeo.email_notifier.domain.TemplateGenerator;
import com.damdamdeo.email_notifier.domain.TodoCreated;
import com.damdamdeo.email_notifier.domain.TodoMarkedAsCompleted;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TemplateGeneratorTest {

    private static class EmailExpected {

        private final String contenuEmailExpected;

        public EmailExpected(final String emailExpectedOnDisk) throws IOException {
            this.contenuEmailExpected = IOUtils.toString(getClass().getClassLoader().getResourceAsStream(emailExpectedOnDisk),
                    StandardCharsets.UTF_8.name());
        }

        public void assertEquals(final String contenu2Check) {
            Assertions.assertEquals(contenuEmailExpected, contenu2Check);
        }

    }

    @Test
    public void should_generate_todo_created_template() throws IOException {
        // Given
        final TemplateGenerator templateGenerator = new PebbleTemplateGenerator();

        // When
        final String contenu = templateGenerator.generate(new TodoCreated() {

            @Override
            public String todoId() {
                return "todoId";
            }

            @Override
            public String description() {
                return "lorem ipsum";
            }

        });

        // Then
        new EmailExpected("todoCreatedExpected.html").assertEquals(contenu);
    }

    @Test
    public void should_generate_todo_marked_as_completed_template() throws IOException {
        // Given
        final TemplateGenerator templateGenerator = new PebbleTemplateGenerator();

        // When
        final String contenu = templateGenerator.generate(new TodoMarkedAsCompleted() {

            @Override
            public String todoId() {
                return "todoId";
            }

            @Override
            public String description() {
                return "lorem ipsum";
            }

        });

        // Then
        new EmailExpected("todoMarketAsCompletedExpected.html").assertEquals(contenu);
    }

}
