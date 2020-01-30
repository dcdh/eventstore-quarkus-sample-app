package com.damdamdeo.todo.domain.api.shared.specification;

public interface Specification<T> {

    boolean isSatisfiedBy(T t);

    Specification<T> and(Specification<T> other);

    default void checkSatisfiedBy(T t) throws UnsatisfiedSpecificationException {
        if (!isSatisfiedBy(t)) {
            throw createUnsatisfiedSpecificationException(t);
        }
    }

    UnsatisfiedSpecificationException createUnsatisfiedSpecificationException(T t);

}

