package com.damdamdeo.e2e;

import io.fabric8.kubernetes.client.dsl.ExecWatch;
import io.fabric8.kubernetes.client.utils.NonBlockingInputStreamPumper;
import io.fabric8.openshift.client.OpenShiftClient;
import org.arquillian.cube.openshift.impl.requirement.RequiresOpenshift;
import org.awaitility.Awaitility;
import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertThat;

// https://github.com/fabric8io/kubernetes-client/tree/master/kubernetes-examples/src/main/java/io/fabric8/kubernetes/examples
@RunAsClient
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
        final InputStream in = System.in;
        final String podName = "broker-kafka-0";
        final SystemOutCallback systemOutCallback = new SystemOutCallback();
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
            execWatch.getInput().write("date +%D\n".getBytes());
            Awaitility.await().atMost(10, TimeUnit.SECONDS).until(() -> systemOutCallback.getData() != null);
            final String expectedDate = LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/YY"));
            assertThat(systemOutCallback.getData(), CoreMatchers.equalTo(expectedDate));
        } finally {
            executorService.shutdownNow();
        }
    }

}
