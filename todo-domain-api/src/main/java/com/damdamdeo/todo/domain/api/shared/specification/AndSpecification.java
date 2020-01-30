package com.damdamdeo.todo.domain.api.shared.specification;

import org.apache.commons.lang3.Validate;

public class AndSpecification<T> extends AbstractSpecification<T> {

    private Specification<T> first;
    private Specification<T> second;

    public AndSpecification(final Specification<T> first, final Specification<T> second) {
        Validate.notNull(first);
        Validate.notNull(second);
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean isSatisfiedBy(final T t) {
        Validate.notNull(t);
        return first.isSatisfiedBy(t) && second.isSatisfiedBy(t);
    }

    @Override
    public UnsatisfiedSpecificationException createUnsatisfiedSpecificationException(final T t) {
        Validate.notNull(t);
        if (!first.isSatisfiedBy(t)) {
            return first.createUnsatisfiedSpecificationException(t);
        } else if (!second.isSatisfiedBy(t)) {
            return second.createUnsatisfiedSpecificationException(t);
        }
        throw new IllegalStateException();
    }

}
