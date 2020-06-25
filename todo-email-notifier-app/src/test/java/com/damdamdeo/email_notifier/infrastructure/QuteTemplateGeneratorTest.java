package com.damdamdeo.email_notifier.infrastructure;

import com.damdamdeo.email_notifier.domain.Todo;
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
        final Todo todo = mock(Todo.class);
        doReturn("todoId").when(todo).todoId();
        doReturn("lorem ipsum").when(todo).description();

        final String contenu = quteTemplateGenerator.generateTodoCreated(todo);

        // Then
        assertEquals(IOUtils.toString(getClass().getClassLoader().getResourceAsStream("todoCreatedExpected.html"), StandardCharsets.UTF_8.name()),
                contenu);
        verify(todo, times(1)).todoId();
        verify(todo, times(1)).description();
        verifyNoMoreInteractions(todo);
    }

    @Test
    public void should_generate_todo_marked_as_completed_template() throws IOException {
        // When
        final Todo todo = mock(Todo.class);
        doReturn("todoId").when(todo).todoId();
        doReturn("lorem ipsum").when(todo).description();

        final String contenu = quteTemplateGenerator.generateTodoMarkedAsCompleted(todo);

        // Then
        assertEquals(IOUtils.toString(getClass().getClassLoader().getResourceAsStream("todoMarkedAsCompletedExpected.html"), StandardCharsets.UTF_8.name()),
                contenu);
        verify(todo, times(1)).todoId();
        verify(todo, times(1)).description();
        verifyNoMoreInteractions(todo);
    }

}
