package com.mdh.alarm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.mdh.alarm.slack.SlackAlertRequest;
import com.mdh.alarm.slack.SlackClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SlackFallbackTest {

    private final SlackClient.SlackFallback slackFallback = new SlackClient.SlackFallback();

    @Mock
    private Throwable mockThrowable;

    @DisplayName("슬랙 api가 예외를 발생하면 fallback에 작성해놓은 메소드가 호출된다.")
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