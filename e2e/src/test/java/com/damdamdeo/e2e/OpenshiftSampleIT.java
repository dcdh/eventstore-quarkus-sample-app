package com.damdamdeo.e2e;

import io.fabric8.kubernetes.client.dsl.ExecWatch;
import io.fabric8.kubernetes.client.utils.BlockingInputStreamPumper;
import io.fabric8.openshift.client.OpenShiftClient;
import org.arquillian.cube.openshift.impl.requirement.RequiresOpenshift;
import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertThat;

@Category(RequiresOpenshift.class)
@RequiresOpenshift
@RunWith(Arquillian.class)
public class OpenshiftSampleIT {

    private static final String NAMESPACE = "e2e";

    @ArquillianResource
    private OpenShiftClient client;

    @Test
    public void should_return_date() throws Exception {
        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        try (final ExecWatch execWatch = client.pods()
                .inNamespace(NAMESPACE)
                .withName("broker-kafka-0")
                .inContainer("kafka")
                .readingInput(System.in)
                .redirectingOutput()
                .redirectingError()
                .redirectingErrorChannel()
                .usingListener(new PodListener())
                .exec("date","+%D")) {
            final SystemOutCallback systemOutCallback = new SystemOutCallback();
            final BlockingInputStreamPumper pump = new BlockingInputStreamPumper(execWatch.getOutput(), systemOutCallback);
            executorService.submit(pump).get();
            final String expectedDate = LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/YY"));
            assertThat(systemOutCallback.getData(), CoreMatchers.equalTo(expectedDate));
        } finally {
            executorService.shutdownNow();
        }
    }

}
