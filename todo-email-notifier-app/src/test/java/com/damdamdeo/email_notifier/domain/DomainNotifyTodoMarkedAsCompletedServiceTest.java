package com.damdamdeo.email_notifier.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DomainNotifyTodoMarkedAsCompletedServiceTest {

    @Test
    public void should_generate_content_when_doing_a_notification() {
        // Given
        final TemplateGenerator templateGenerator = mock(TemplateGenerator.class);
        final EmailNotifier emailNotifier = mock(EmailNotifier.class);
        final TodoMarkedAsCompletedNotifierService todoMarkedAsCompletedNotifierService = new DomainTodoMarkedAsCompletedNotifierService(templateGenerator,
                emailNotifier);
        final TodoDomain todoDomain = buildTodoDomain4Test().build();

        // When
        todoMarkedAsCompletedNotifierService.notify(todoDomain);

        // Then
        verify(templateGenerator, times(1)).generateTodoMarkedAsCompleted(todoDomain);
    }

    @Test
    public void should_notify_using_expected_values_when_doing_a_notification() {
        // Given
        final TemplateGenerator templateGenerator = mock(TemplateGenerator.class);
        final EmailNotifier emailNotifier = mock(EmailNotifier.class);
        final TodoMarkedAsCompletedNotifierService todoMarkedAsCompletedNotifierService = new DomainTodoMarkedAsCompletedNotifierService(templateGenerator,
                emailNotifier);
        final TodoDomain todoDomain = buildTodoDomain4Test().build();

        doReturn("content").when(templateGenerator).generateTodoMarkedAsCompleted(todoDomain);

        // When
        todoMarkedAsCompletedNotifierService.notify(todoDomain);

        // Then
        verify(emailNotifier, times(1)).notify("Todo marked as completed", "content");
        verify(templateGenerator, times(1)).generateTodoMarkedAsCompleted(any());
    }

    private TodoDomain.Builder buildTodoDomain4Test() {
        return TodoDomain.newBuilder()
                .withTodoId("todoId")
                .withDescription("description");
    }
}
