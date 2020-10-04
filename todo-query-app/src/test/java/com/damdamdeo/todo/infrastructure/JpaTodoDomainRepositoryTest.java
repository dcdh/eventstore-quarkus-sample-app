package com.damdamdeo.todo.infrastructure;

import com.damdamdeo.todo.domain.TodoDomain;
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
public class JpaTodoDomainRepositoryTest {

    @Inject
    JpaTodoDomainRepository jpaTodoRepository;

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
        final TodoEntity todoToCreate = TodoEntity.newBuilder()
                .withTodoId("todoId")
                .withDescription("lorem ipsum")
                .withTodoStatus(TodoStatus.IN_PROGRESS)
                .withVersion(0l).build();

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
        assertTrue(EqualsBuilder.reflectionEquals(TodoEntity.newBuilder()
                        .withTodoId("todoId")
                        .withDescription("lorem ipsum")
                        .withTodoStatus(TodoStatus.IN_PROGRESS)
                        .withVersion(0l).build(),
                todos.get(0)));
    }

    @Test
    public void should_get_todo_return_expected_todo() throws Exception {
        // Given
        final TodoEntity todoToCreate = TodoEntity.newBuilder()
                .withTodoId("todoId")
                .withDescription("lorem ipsum")
                .withTodoStatus(TodoStatus.IN_PROGRESS)
                .withVersion(0l).build();
        userTransaction.begin();
        entityManager.persist(todoToCreate);
        userTransaction.commit();

        // When
        final TodoDomain todoPersisted = jpaTodoRepository.get("todoId");

        // Then
        assertEquals(todoPersisted, todoToCreate.toDomain());
    }

    @Test
    public void should_fetch_all_todos() throws Exception {
        // Given
        final TodoEntity todoToCreate = TodoEntity.newBuilder()
                .withTodoId("todoId")
                .withDescription("lorem ipsum")
                .withTodoStatus(TodoStatus.IN_PROGRESS)
                .withVersion(0l).build();
        userTransaction.begin();
        entityManager.persist(todoToCreate);
        userTransaction.commit();

        // When
        final List<TodoDomain> todos = jpaTodoRepository.fetchAll();

        // Then
        assertEquals(1, todos.size());
        assertEquals(todos.get(0), todoToCreate.toDomain());
    }

    @Test
    public void should_persist() {
        // Given
        final TodoDomain todoDomain = TodoDomain.newBuilder()
                .withTodoId("todoId")
                .withDescription("lorem ipsum")
                .withTodoStatus(TodoStatus.IN_PROGRESS)
                .withVersion(0l).build();

        // When
        final TodoDomain persisted = jpaTodoRepository.persist(todoDomain);

        // Then
        assertEquals(TodoDomain.newBuilder()
                .withTodoId("todoId")
                .withDescription("lorem ipsum")
                .withTodoStatus(TodoStatus.IN_PROGRESS)
                .withVersion(0l).build(), persisted);
        assertEquals(persisted, entityManager.find(TodoEntity.class, "todoId").toDomain());
    }

    @Test
    public void should_merge() {
        final TodoDomain todoDomain = TodoDomain.newBuilder()
                .withTodoId("todoId")
                .withDescription("lorem ipsum")
                .withTodoStatus(TodoStatus.IN_PROGRESS)
                .withVersion(0l).build();

        // When
        final TodoDomain merged = jpaTodoRepository.merge(todoDomain);

        // Then
        assertEquals(TodoDomain.newBuilder()
                .withTodoId("todoId")
                .withDescription("lorem ipsum")
                .withTodoStatus(TodoStatus.IN_PROGRESS)
                .withVersion(0l).build(), merged);
        assertEquals(merged, entityManager.find(TodoEntity.class, "todoId").toDomain());
    }

}
