package com.damdamdeo.email_notifier.infrastructure.template_generator;

import com.damdamdeo.email_notifier.domain.TodoDomain;
import io.quarkus.test.junit.QuarkusTest;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@QuarkusTest
public class QuteTemplateGeneratorTest {

    @Inject
    QuteTemplateGenerator quteTemplateGenerator;

    @Test
    public void should_generate_todo_created_template() throws IOException {
        // When
        final TodoDomain todoDomain = mock(TodoDomain.class);
        doReturn("todoId").when(todoDomain).todoId();
        doReturn("lorem ipsum").when(todoDomain).description();

        final String contenu = quteTemplateGenerator.generateTodoCreated(todoDomain);

        // Then
        assertEquals(IOUtils.toString(getClass().getClassLoader().getResourceAsStream("todoCreatedExpected.html"), StandardCharsets.UTF_8.name()),
                contenu);
        verify(todoDomain, times(1)).todoId();
        verify(todoDomain, times(1)).description();
        verifyNoMoreInteractions(todoDomain);
    }

    @Test
    public void should_generate_todo_marked_as_completed_template() throws IOException {
        // When
        final TodoDomain todoDomain = mock(TodoDomain.class);
        doReturn("todoId").when(todoDomain).todoId();
        doReturn("lorem ipsum").when(todoDomain).description();

        final String contenu = quteTemplateGenerator.generateTodoMarkedAsCompleted(todoDomain);

        // Then
        assertEquals(IOUtils.toString(getClass().getClassLoader().getResourceAsStream("todoMarkedAsCompletedExpected.html"), StandardCharsets.UTF_8.name()),
                contenu);
        verify(todoDomain, times(1)).todoId();
        verify(todoDomain, times(1)).description();
        verifyNoMoreInteractions(todoDomain);
    }

}
