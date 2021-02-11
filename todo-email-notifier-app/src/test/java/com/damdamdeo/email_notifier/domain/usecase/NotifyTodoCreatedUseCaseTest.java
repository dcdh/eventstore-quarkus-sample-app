package com.damdamdeo.email_notifier.domain.usecase;

import com.damdamdeo.email_notifier.domain.EmailNotifier;
import com.damdamdeo.email_notifier.domain.TemplateGenerator;
import com.damdamdeo.email_notifier.domain.TodoDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotifyTodoCreatedUseCaseTest {

    @Test
    public void should_generate_content_when_doing_a_notification() {
        // Given
        final TemplateGenerator templateGenerator = mock(TemplateGenerator.class);
        final EmailNotifier emailNotifier = mock(EmailNotifier.class);
        final NotifyTodoCreatedUseCase notifyTodoCreatedUseCase = new NotifyTodoCreatedUseCase(templateGenerator,
                emailNotifier);
        final TodoDomain todoDomain = buildTodoDomain4Test().build();

        // When
        notifyTodoCreatedUseCase.execute(new NotifyTodoCreatedCommand(todoDomain));

        // Then
        verify(templateGenerator, times(1)).generateTodoCreated(todoDomain);
    }

    @Test
    public void should_notify_using_expected_values_when_doing_a_notification() {
        // Given
        final TemplateGenerator templateGenerator = mock(TemplateGenerator.class);
        final EmailNotifier emailNotifier = mock(EmailNotifier.class);
        final NotifyTodoCreatedUseCase notifyTodoCreatedUseCase = new NotifyTodoCreatedUseCase(templateGenerator,
                emailNotifier);
        final TodoDomain todoDomain = buildTodoDomain4Test().build();

        doReturn("content").when(templateGenerator).generateTodoCreated(todoDomain);

        // When
        notifyTodoCreatedUseCase.execute(new NotifyTodoCreatedCommand(todoDomain));

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
