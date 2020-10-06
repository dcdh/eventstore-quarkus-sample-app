package com.damdamdeo.email_notifier.domain;

import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class DomainTodoCreatedNotifierServiceTest {

    @Test
    public void should_generate_content_when_doing_a_notification() {
        // Given
        final TemplateGenerator templateGenerator = mock(TemplateGenerator.class);
        final EmailNotifier emailNotifier = mock(EmailNotifier.class);
        final TodoCreatedNotifierService todoCreatedNotifierService = new DomainTodoCreatedNotifierService(templateGenerator,
                emailNotifier);
        final TodoDomain todoDomain = buildTodoDomain4Test().build();

        // When
        todoCreatedNotifierService.notify(todoDomain);

        // Then
        verify(templateGenerator, times(1)).generateTodoCreated(todoDomain);
    }

    @Test
    public void should_notify_using_expected_values_when_doing_a_notification() {
        // Given
        final TemplateGenerator templateGenerator = mock(TemplateGenerator.class);
        final EmailNotifier emailNotifier = mock(EmailNotifier.class);
        final TodoCreatedNotifierService todoCreatedNotifierService = new DomainTodoCreatedNotifierService(templateGenerator,
                emailNotifier);
        final TodoDomain todoDomain = buildTodoDomain4Test().build();

        doReturn("content").when(templateGenerator).generateTodoCreated(todoDomain);

        // When
        todoCreatedNotifierService.notify(todoDomain);

        // Then
        verify(emailNotifier, times(1)).notify("New Todo created", "content");
        verify(templateGenerator, times(1)).generateTodoCreated(any());
    }

    private TodoDomain.Builder buildTodoDomain4Test() {
        return TodoDomain.newBuilder()
                .withTodoId("todoId")
                .withDescription("description");
    }

}
