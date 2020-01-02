package com.damdamdeo.todo_graph_visualiser.interfaces;

import com.damdamdeo.todo_graph_visualiser.domain.GraphRepository;
import com.damdamdeo.todo_graph_visualiser.domain.Todo;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/graph")
@Produces(MediaType.APPLICATION_JSON)
public class GraphEndpoint {

    @Inject
    GraphRepository graphRepository;

    @GET
    public List<Todo> getTodos() {
        return graphRepository.getAll();
    }

}
