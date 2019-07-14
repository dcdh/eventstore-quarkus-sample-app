package com.damdamdeo.todo.domain;

import com.damdamdeo.todo.api.Todo;
import com.damdamdeo.todo.api.TodoAggregateRootRepository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@ApplicationScoped
public class TodoCommandHandler {

    private ExecutorService exactlyOnceCommandExecutor;
    private List<ExecutorService> threadPools;
    private Set<String> handledAggregateRootIdsInExactlyOnce;

    final TodoAggregateRootRepository todoAggregateRootRepository;

    public TodoCommandHandler(final TodoAggregateRootRepository todoAggregateRootRepository) {
        this.todoAggregateRootRepository = Objects.requireNonNull(todoAggregateRootRepository);
    }

    @PostConstruct
    public void init() {
        this.exactlyOnceCommandExecutor = Executors.newSingleThreadExecutor();
        final List<ExecutorService> list = IntStream
                .range(0, 20)
                .mapToObj(i -> Executors.newSingleThreadExecutor())
                .collect(Collectors.toList());
        this.threadPools = new CopyOnWriteArrayList<>(list);
        this.handledAggregateRootIdsInExactlyOnce = Collections.synchronizedSet(new HashSet<>());
    }

    @PreDestroy
    public void destroy() {
        this.exactlyOnceCommandExecutor.shutdown();
        this.threadPools.stream().forEach(ExecutorService::shutdown);
    }

    @Transactional
    public Todo handle(final TodoCommand todoCommand) throws Throwable {
        final ExecutorService executorServiceToExecuteCommand;
        if (todoCommand.exactlyOnceCommandExecution()) {
            executorServiceToExecuteCommand = this.exactlyOnceCommandExecutor;
            this.handledAggregateRootIdsInExactlyOnce.add(todoCommand.todoId());
        } else if (this.handledAggregateRootIdsInExactlyOnce.contains(todoCommand.todoId())) {
            executorServiceToExecuteCommand = this.exactlyOnceCommandExecutor;
        } else if (todoCommand.todoId() == null) {
            executorServiceToExecuteCommand = this.threadPools.get(0);
        } else {
            final int threadIdx = Math.abs(todoCommand.todoId().hashCode()) % threadPools.size();
            executorServiceToExecuteCommand = threadPools.get(threadIdx);
        }
        try {
            return executorServiceToExecuteCommand.submit(() -> {
                final Todo todo = todoCommand.handle(todoAggregateRootRepository);
                this.handledAggregateRootIdsInExactlyOnce.remove(todo.todoId());
                return todo;
            }).get();
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        } catch (final ExecutionException e) {
            throw e.getCause();
        }
    }

}
