package com.damdamdeo.todo.infrastructure;

import com.damdamdeo.todo.domain.api.Todo;
import com.damdamdeo.todo.domain.api.TodoStatus;
import io.quarkus.test.junit.QuarkusTest;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class JpaTodoRepositoryTest {

    @Inject
    JpaTodoRepository jpaTodoRepository;

    @Inject
    EntityManager entityManager;

    @Inject
    UserTransaction userTransaction;

    @BeforeEach
    @AfterEach
    public void flush() throws Exception {
        userTransaction.begin();
        entityManager.createQuery("DELETE FROM TodoEntity").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE todoentity_aud CASCADE").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE revinfo CASCADE").executeUpdate();
        entityManager.createNativeQuery("ALTER SEQUENCE public.hibernate_sequence RESTART WITH 1");
        userTransaction.commit();
    }

    @Test
    public void should_audit_todo() throws Exception {
        // Given
        final TodoEntity todoToCreate = new TodoEntity("todoId", "lorem ipsum", TodoStatus.IN_PROGRESS, 0l);

        // When
        userTransaction.begin();
        entityManager.persist(todoToCreate);
        userTransaction.commit();

        // Then
        final List<TodoEntity> todos = AuditReaderFactory.get(entityManager)
                .createQuery()
                .forRevisionsOfEntity(TodoEntity.class, true, true)
                .add(AuditEntity.id().eq("todoId"))
                .getResultList();
        assertEquals(1, todos.size());
        assertTrue(EqualsBuilder.reflectionEquals(new TodoEntity("todoId", "lorem ipsum", TodoStatus.IN_PROGRESS, 0l),
                todos.get(0)));
    }

    @Test
    public void should_get_todo_return_expected_todo() throws Exception {
        // Given
        final TodoEntity todoToCreate = new TodoEntity("todoId", "lorem ipsum", TodoStatus.IN_PROGRESS, 0l);
        userTransaction.begin();
        entityManager.persist(todoToCreate);
        userTransaction.commit();

        // When
        final Todo todoPersisted = jpaTodoRepository.get("todoId");

        // Then
        assertTrue(EqualsBuilder.reflectionEquals(todoPersisted, todoToCreate));
    }

    @Test
    public void should_fetch_all_todos() throws Exception {
        // Given
        final TodoEntity todoToCreate = new TodoEntity("todoId", "lorem ipsum", TodoStatus.IN_PROGRESS, 0l);
        userTransaction.begin();
        entityManager.persist(todoToCreate);
        userTransaction.commit();

        // When
        final List<Todo> todos = jpaTodoRepository.fetchAll();

        // Then
        assertEquals(1, todos.size());
        assertTrue(EqualsBuilder.reflectionEquals(todos.get(0), todoToCreate));
    }

}
