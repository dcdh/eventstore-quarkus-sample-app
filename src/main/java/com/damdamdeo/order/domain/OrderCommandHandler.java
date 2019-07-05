package com.damdamdeo.order.domain;

import com.damdamdeo.order.api.Order;
import com.damdamdeo.order.api.OrderAggregateRootRepository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@ApplicationScoped
public class OrderCommandHandler {

    private ExecutorService exactlyOnceCommandExecutor;
    private List<ExecutorService> threadPools;
    private Set<String> handledAggregateRootIdsInExactlyOnce;

    final OrderAggregateRootRepository orderAggregateRootRepository;

    public OrderCommandHandler(final OrderAggregateRootRepository orderAggregateRootRepository) {
        this.orderAggregateRootRepository = Objects.requireNonNull(orderAggregateRootRepository);
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
    public Order handle(final OrderCommand orderCommand) throws Throwable {
        final ExecutorService executorServiceToExecuteCommand;
        if (orderCommand.exactlyOnceCommandExecution()) {
            executorServiceToExecuteCommand = this.exactlyOnceCommandExecutor;
            this.handledAggregateRootIdsInExactlyOnce.add(orderCommand.orderId());
        } else if (this.handledAggregateRootIdsInExactlyOnce.contains(orderCommand.orderId())) {
            executorServiceToExecuteCommand = this.exactlyOnceCommandExecutor;
        } else if (orderCommand.orderId() == null) {
            executorServiceToExecuteCommand = this.threadPools.get(0);
        } else {
            final int threadIdx = Math.abs(orderCommand.orderId().hashCode()) % threadPools.size();
            executorServiceToExecuteCommand = threadPools.get(threadIdx);
        }
        try {
            return executorServiceToExecuteCommand.submit(() -> {
                final Order order = orderCommand.handle(orderAggregateRootRepository);
                this.handledAggregateRootIdsInExactlyOnce.remove(order.orderId());
                return order;
            }).get();
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        } catch (final ExecutionException e) {
            throw e.getCause();
        }
    }

}
