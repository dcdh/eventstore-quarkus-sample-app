package com.damdamdeo.todo.domain.api.shared.specification;

public abstract class AbstractSpecification<T> implements Specification<T> {

    @Override
    public Specification<T> and(final Specification<T> other) {
        return new AndSpecification<>(this, other);
    }

}
