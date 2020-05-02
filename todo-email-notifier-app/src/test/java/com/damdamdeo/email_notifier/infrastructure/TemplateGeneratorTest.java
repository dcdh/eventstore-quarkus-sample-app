package com.damdamdeo.email_notifier.infrastructure;

import com.damdamdeo.email_notifier.domain.TemplateGenerator;
import com.damdamdeo.email_notifier.domain.TodoCreated;
import com.damdamdeo.email_notifier.domain.TodoMarkedAsCompleted;
import io.quarkus.test.junit.QuarkusTest;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class TemplateGeneratorTest {

    @Inject
    TemplateGenerator templateGenerator;

    @Test
    public void should_generate_todo_created_template() throws IOException {
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
        assertEquals(IOUtils.toString(getClass().getClassLoader().getResourceAsStream("todoCreatedExpected.html"), StandardCharsets.UTF_8.name()),
                contenu);
    }

    @Test
    public void should_generate_todo_marked_as_completed_template() throws IOException {
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
        assertEquals(IOUtils.toString(getClass().getClassLoader().getResourceAsStream("todoMarkedAsCompletedExpected.html"), StandardCharsets.UTF_8.name()),
                contenu);
    }

}
