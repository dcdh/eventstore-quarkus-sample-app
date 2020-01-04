package com.damdamdeo.e2e;

import com.jayway.restassured.RestAssured;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.dsl.ExecWatch;
import io.fabric8.kubernetes.client.utils.NonBlockingInputStreamPumper;
import io.fabric8.openshift.client.OpenShiftClient;
import org.arquillian.cube.openshift.impl.enricher.RouteURL;
import org.arquillian.cube.openshift.impl.requirement.RequiresOpenshift;
import org.awaitility.Awaitility;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.experimental.categories.Category;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.is;

@Category(RequiresOpenshift.class)
@RequiresOpenshift
public class TodoFeatureStepsIT {

    private static final String NAMESPACE = "e2e";

    @ArquillianResource
    private OpenShiftClient client;

    @RouteURL(value = "mailhog")
    private URL mailhog;

    @RouteURL(value = "todo-write-app")
    private URL write;

    @RouteURL(value = "todo-query-app")
    private URL query;

    @RouteURL(value = "todo-graph-visualiser-app")
    private URL graph;

    @Before
    public void flush_kafka() throws Exception {
        /**
         * bin/kafka-topics.sh --delete --bootstrap-server localhost:9092 --topic sampleTopic ; \
         * bin/kafka-topics.sh --list --bootstrap-server localhost:9092 ; \
         * bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic sampleTopic ; \
         * bin/kafka-topics.sh --list --bootstrap-server localhost:9092
         *
         * bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test --property print.key=true --from-beginning
         */
        final String podName = "broker-kafka-0";
        final SystemOutCallback systemOutCallback = new SystemOutCallback();
        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        try (final ExecWatch execWatch = client.pods()
                .inNamespace(NAMESPACE)
                .withName(podName)
                .inContainer("kafka")
                .redirectingInput()
                .redirectingOutput()
                .redirectingError()
                .redirectingErrorChannel()
                .usingListener(new PodListener(podName))
                .exec();
             final NonBlockingInputStreamPumper pump = new NonBlockingInputStreamPumper(execWatch.getOutput(), systemOutCallback)
        ) {
            executorService.submit(pump);
            final String topicEvent = "event";
            final String topicAggregaterootprojection = "aggregaterootprojection";
            for (final String cmd : Arrays.asList("bash -i -c 'bin/kafka-topics.sh --delete --bootstrap-server localhost:9092 --topic " + topicEvent + "; echo DONE'\n",
                    "bash -i -c 'bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic " + topicEvent + "; echo DONE'\n",
                    "bash -i -c 'bin/kafka-topics.sh --delete --bootstrap-server localhost:9092 --topic " + topicAggregaterootprojection + "; echo DONE'\n",
                    "bash -i -c 'bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic " + topicAggregaterootprojection + "; echo DONE'\n")) {
                try {
                    execWatch.getInput().write(cmd.getBytes());
                    // Wait that commands have been executed by checking that "DONE" was outputted in 10 seconds or "does not exist" which means that an un-existent topic has been deleted
                    Awaitility.await()
                            .atMost(10, TimeUnit.SECONDS).until(() ->
                            systemOutCallback.getData() != null
                                    && (systemOutCallback.getData().endsWith("DONE") || systemOutCallback.getData().contains("does not exist")));
                } catch (final Exception exception) {
                    System.err.print("Exception");
                    try (final BufferedReader stdInput = new BufferedReader(new InputStreamReader(execWatch.getError()))) {
                        char[] buff = new char[1024];
                        for (int n; (n = stdInput.read(buff)) != -1; ) {
                            System.err.print(new String(buff, 0, n));
                        }
                    }
                    throw exception;
                }
            }
        } finally {
            executorService.shutdownNow();
        }
    }

    @Before
    public void flush_todo_email_notifier_database() throws Exception {
        /**
         * oc exec todo-email-notifier-1-ggv85 -c postgresql -- scl enable rh-postgresql10 -- psql -U postgresuser -d todo-email-notifier -c 'TRUNCATE eventconsumerconsumed, eventconsumed, revinfo, todoentity_aud, todoentity'
         * oc exec todo-email-notifier-1-ggv85 -c postgresql -- bash -c 'psql -U postgresuser -d todo-email-notifier -c "TRUNCATE eventconsumerconsumed, eventconsumed, revinfo, todoentity_aud, todoentity"'
         */
        final String podName = client.pods().inNamespace(NAMESPACE).withLabel("name", "todo-email-notifier").list().getItems().stream().findFirst().map(Pod::getMetadata).map(ObjectMeta::getName).get();
        final SystemOutCallback systemOutCallback = new SystemOutCallback();
        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        try (final ExecWatch execWatch = client.pods()
                .inNamespace(NAMESPACE)
                .withName(podName)
                .inContainer("postgresql")
                .redirectingInput()
                .redirectingOutput()
                .redirectingError()
                .redirectingErrorChannel()
                .usingListener(new PodListener(podName))
                .exec();
             final NonBlockingInputStreamPumper pump = new NonBlockingInputStreamPumper(execWatch.getOutput(), systemOutCallback)
        ) {
            executorService.submit(pump);
            execWatch.getInput().write("bash -c 'psql -U postgresuser -d todo-email-notifier -c \"TRUNCATE eventconsumerconsumed, eventconsumed, revinfo, todoentity_aud, todoentity\"'\n".getBytes());
            Awaitility.await().atMost(10, TimeUnit.SECONDS).until(() -> systemOutCallback.getData() != null && systemOutCallback.getData().endsWith("TRUNCATE TABLE"));
        } finally {
            executorService.shutdownNow();
        }
    }

    @Before
    public void flush_todo_query_database() throws Exception {
        // psql -U postgresuser -d todo-query -c "TRUNCATE eventconsumerconsumed, eventconsumed, revinfo, todoentity_aud, todoentity;"
        final String podName = client.pods().inNamespace(NAMESPACE).withLabel("name", "todo-query").list().getItems().stream().findFirst().map(Pod::getMetadata).map(ObjectMeta::getName).get();
        final SystemOutCallback systemOutCallback = new SystemOutCallback();
        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        try (final ExecWatch execWatch = client.pods()
                .inNamespace(NAMESPACE)
                .withName(podName)
                .inContainer("postgresql")
                .redirectingInput()
                .redirectingOutput()
                .redirectingError()
                .redirectingErrorChannel()
                .usingListener(new PodListener(podName))
                .exec();
             final NonBlockingInputStreamPumper pump = new NonBlockingInputStreamPumper(execWatch.getOutput(), systemOutCallback)
        ) {
            executorService.submit(pump);
            execWatch.getInput().write("bash -c 'psql -U postgresuser -d todo-query -c \"TRUNCATE eventconsumerconsumed, eventconsumed, revinfo, todoentity_aud, todoentity\"'\n".getBytes());
            Awaitility.await().atMost(10, TimeUnit.SECONDS).until(() -> systemOutCallback.getData() != null && systemOutCallback.getData().endsWith("TRUNCATE TABLE"));
        } finally {
            executorService.shutdownNow();
        }
    }

    @Before
    public void flush_eventstore() throws Exception {
        // psql -U postgresuser -d eventstore -c "TRUNCATE eventconsumerconsumed, eventconsumed, event, aggregaterootprojection;"
        final String podName = client.pods().inNamespace(NAMESPACE).withLabel("name", "eventstore").list().getItems().stream().findFirst().map(Pod::getMetadata).map(ObjectMeta::getName).get();
        final SystemOutCallback systemOutCallback = new SystemOutCallback();
        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        try (final ExecWatch execWatch = client.pods()
                .inNamespace(NAMESPACE)
                .withName(podName)
                .inContainer("postgresql")
                .redirectingInput()
                .redirectingOutput()
                .redirectingError()
                .redirectingErrorChannel()
                .usingListener(new PodListener(podName))
                .exec();
             final NonBlockingInputStreamPumper pump = new NonBlockingInputStreamPumper(execWatch.getOutput(), systemOutCallback)
        ) {
            executorService.submit(pump);
            execWatch.getInput().write("bash -c 'psql -U postgresuser -d eventstore -c \"TRUNCATE eventconsumerconsumed, eventconsumed, event, aggregaterootprojection\"'\n".getBytes());
            Awaitility.await().atMost(10, TimeUnit.SECONDS).until(() -> systemOutCallback.getData() != null && systemOutCallback.getData().endsWith("TRUNCATE TABLE"));
        } finally {
            executorService.shutdownNow();
        }
    }

    @Before
    public void flush_mailhog() {
        RestAssured.when()
                .delete(mailhog + "api/v1/messages")
                .prettyPeek()
                .then()
                .statusCode(200);
    }

    @Before
    public void flush_graph() throws Exception {
        final String podName = client.pods().inNamespace(NAMESPACE).withLabel("name", "neo4j").list().getItems().stream().findFirst().map(Pod::getMetadata).map(ObjectMeta::getName).get();
        final SystemOutCallback systemOutCallback = new SystemOutCallback();
        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        try (final ExecWatch execWatch = client.pods()
                .inNamespace(NAMESPACE)
                .withName(podName)
                .inContainer("neo4j")
                .redirectingInput()
                .redirectingOutput()
                .redirectingError()
                .redirectingErrorChannel()
                .usingListener(new PodListener(podName))
                .exec();
             final NonBlockingInputStreamPumper pump = new NonBlockingInputStreamPumper(execWatch.getOutput(), systemOutCallback)
        ) {
            executorService.submit(pump);
            execWatch.getInput().write("bash -c 'bin/cypher-shell -u neo4j -p secret \"MATCH (n) DETACH DELETE n\";echo DONE'\n".getBytes());
            Awaitility.await().atMost(10, TimeUnit.SECONDS).until(() -> systemOutCallback.getData() != null && systemOutCallback.getData().endsWith("DONE"));
        } finally {
            executorService.shutdownNow();
        }
    }

    @When("^I create a todo$")
    public void i_create_a_todo() {
        RestAssured
                .given()
                .formParams("todoId", "todoId", "description", "lorem ipsum")
                .when()
                .post(write + "todos/createNewTodo")
                .prettyPeek()
                .then()
                .statusCode(200);
    }

    @Then("^A todo is created$")
    public void a_todo_is_created() {
        // je dois utiliser awaitability until statut = 200
        await().atMost(10, TimeUnit.SECONDS).until(() ->
                        RestAssured.given().get(query + "todos/todoId")
                                .prettyPeek()
                                .then()
                                .extract()
                                .statusCode() == 200);
        final String todoId = RestAssured.given().get(query + "todos/todoId")
                .prettyPeek()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath()
                .getString("todoId");
        assertThat(todoId).isEqualTo("todoId");
        await().atMost(10, TimeUnit.SECONDS).until(() ->
                RestAssured.given().get(graph + "graph")
                        .prettyPeek()
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .jsonPath().getList("$.todos").size() > 0);
        RestAssured.given().get(graph + "graph")
                .prettyPeek()
                .then()
                .statusCode(200)
                .body("$.todos[0].todoId", is("todoId"))
                .body("$.todos[0].todoStatus", is("IN_PROGRESS"));
    }

    @Then("^A created todo mail notification is sent$")
    public void a_created_todo_mail_notification_is_sent() {
        await().atMost(10, TimeUnit.SECONDS).until(() ->
                RestAssured.given()
                        .get(mailhog + "api/v1/messages")
                        .prettyPeek()
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .jsonPath()
                        .getString("[0].Content.Headers.Subject[0]").equals("New Todo created"));
    }

    @Given("^A created todo$")
    public void a_created_todo() {
        RestAssured
                .given()
                .formParams("todoId", "todoId", "description", "lorem ipsum")
                .when()
                .post(write + "todos/createNewTodo")
                .prettyPeek()
                .then()
                .statusCode(200);
    }

    @When("^I mark the todo as completed$")
    public void i_mark_the_todo_as_completed() {
        RestAssured
                .given()
                .formParams("todoId", "todoId")
                .when()
                .post(write + "todos/markTodoAsCompleted")
                .prettyPeek()
                .then()
                .statusCode(200);
    }

    @Then("^The todo is marked as completed$")
    public void the_todo_is_marked_as_completed() {
        await().atMost(10, TimeUnit.SECONDS).until(() ->
                RestAssured.given().get(query + "todos/todoId")
                        .prettyPeek()
                        .then()
                        .extract()
                        .statusCode() == 200);
        await().atMost(10, TimeUnit.SECONDS).until(() ->
                RestAssured.given().get(query + "todos/todoId")
                        .prettyPeek()
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .jsonPath()
                        .getString("todoStatus")
                        .equals("COMPLETED")
        );
        await().atMost(10, TimeUnit.SECONDS).until(() ->
                RestAssured.given().get(graph + "graph")
                        .prettyPeek()
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .jsonPath().getList("$.todos").size() > 0);
        await().atMost(10, TimeUnit.SECONDS).until(() ->
                RestAssured.given().get(graph + "graph")
                        .prettyPeek()
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .jsonPath()
                        .getString("$.todos[0].todoStatus")
                        .equals("COMPLETED")
        );
    }

    @Then("^A marked todo mail notification is sent$")
    public void a_marked_todo_mail_notification_is_sent() {
        await().atMost(10, TimeUnit.SECONDS).until(() ->
                RestAssured.given()
                        .get(mailhog + "api/v1/messages")
                        .prettyPeek()
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .jsonPath()
                        .getString("[0].Content.Headers.Subject[0]").equals("Todo marked as completed"));
    }

}
