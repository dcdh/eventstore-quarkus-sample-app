package com.damdamdeo.email_notifier.infrastructure.email_notifier;

import io.quarkus.mailer.Mailer;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectSpy;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@QuarkusTest
public class MailerEmailNotificationTest {

    @Inject
    MailerEmailNotification mailerEmailNotification;

    @ConfigProperty(name = "sendTo")
    String sendTo;

    @InjectSpy
    Mailer spyMailer;

    @ConfigProperty(name = "quarkus.mailer.api.port")
    String quarkusMailerApiPort;

    @BeforeEach
    public void cleanMessages() {
        given()
                .when()
                .delete("http://localhost:" + quarkusMailerApiPort + "/api/v1/messages")
                .then()
                .statusCode(200);
    }

    @Test
    public void should_send_email() {
        // Given

        // When
        mailerEmailNotification.notify("subject", "content");

        // Then
        given()
                .when()
                .get("http://localhost:" + quarkusMailerApiPort + "/api/v1/messages")
                .then()
                .log().all()
                .statusCode(200)
                .body("[0].Content.Headers.Subject[0]", equalTo("subject"))
                .body("[0].Content.Headers.From[0]", equalTo("test@quarkus.io"))
                .body("[0].Content.Headers.To[0]", equalTo(sendTo))
                .body("[0].MIME.Parts[0].MIME.Parts[0].Body", equalTo("content"));
        assertEquals("damien.clementdhuart@gmail.com", sendTo);
        // mailer.send(Mail... mails)
        // varargs are not supported by mockito ... :S can't test
        // https://github.com/mockito/mockito/issues/584
//        final ArgumentCaptor<Mail> sendCaptor = ArgumentCaptor.forClass(Mail.class);
//        verify(mailer, Mockito.times(1)).send(sendCaptor.capture());
//        assertTrue(EqualsBuilder.reflectionEquals(Mail.withHtml(sendTo, "subject", "content"), sendCaptor.getValue()));
        verify(spyMailer, times(1)).send(any());
    }

}
