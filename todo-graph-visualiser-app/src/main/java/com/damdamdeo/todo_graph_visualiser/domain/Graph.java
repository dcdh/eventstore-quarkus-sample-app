package com.damdamdeo.todo_graph_visualiser.domain;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class Graph {

    private final List<Todo> todos;

    private final List<Map<String, Object>> events;

    public Graph(final List<Todo> todos, final List<Map<String, Object>> events) {
        this.todos = todos;
        this.events = events;
    }

    public List<Todo> getTodos() {
        return todos;
    }

    public List<Map<String, Object>> getEvents() {
        return events;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Graph)) return false;
        Graph graph = (Graph) o;
        return Objects.equals(todos, graph.todos) &&
                Objects.equals(events, graph.events);
    }

    @Override
    public int hashCode() {
        return Objects.hash(todos, events);
    }
}
