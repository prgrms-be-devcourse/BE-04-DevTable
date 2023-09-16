package com.mdh.alarm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.mdh.alarm.common.api.SlackAlertRequest;
import com.mdh.alarm.common.api.SlackClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

@ExtendWith(MockitoExtension.class)
class SlackFallbackTest {

    private final SlackClient.SlackFallback slackFallback = new SlackClient.SlackFallback();

    @Mock
    private Throwable mockThrowable;



    @Test
    void shouldReturnLoadFailedMessage() {
        // Given
        given(mockThrowable.getMessage()).willReturn("Some error message");
        var slackClient = slackFallback.create(mockThrowable);
        var mockRequest = new SlackAlertRequest("sadf");

        // When
        var result = slackClient.requestAlert(mockRequest);

        // Then
        assertThat(result).isEqualTo("load failed");
    }
}