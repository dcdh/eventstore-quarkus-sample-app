package com.damdamdeo.email_notifier.infrastructure;

import com.damdamdeo.email_notifier.domain.TemplateGenerator;
import com.damdamdeo.email_notifier.domain.TodoCreated;
import com.damdamdeo.email_notifier.domain.TodoMarkedAsCompleted;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class PebbleTemplateGenerator implements TemplateGenerator {

    private static final PebbleTemplate todoCreatedTemplate;
    private static final PebbleTemplate todoMarketAsCompleted;

    static {
        final PebbleEngine engine = new PebbleEngine.Builder().build();
        todoCreatedTemplate = engine.getTemplate("templates/todoCreated.html");
        todoMarketAsCompleted = engine.getTemplate("templates/todoMarketAsCompleted.html");
    }

    @Override
    public String generate(final TodoCreated todoCreated) throws IOException {
        final Writer writer = new StringWriter();
        final Map<String, Object> context = new HashMap<>();
        context.put("todoId", todoCreated.todoId());
        context.put("description", todoCreated.description());

        todoCreatedTemplate.evaluate(writer, context);

        return writer.toString();
    }

    @Override
    public String generate(final TodoMarkedAsCompleted todoMarkedAsCompleted) throws IOException {
        final Writer writer = new StringWriter();
        final Map<String, Object> context = new HashMap<>();
        context.put("todoId", todoMarkedAsCompleted.todoId());
        context.put("description", todoMarkedAsCompleted.description());

        todoMarketAsCompleted.evaluate(writer, context);

        return writer.toString();
    }

}
