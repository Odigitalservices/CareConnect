package com.careconnect.shared.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ApiResponseTest {

    @Test
    void success_wraps_data_with_success_true() {
        var response = ApiResponse.success("hello");
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).isEqualTo("hello");
        assertThat(response.getMessage()).isNull();
    }

    @Test
    void error_sets_success_false_and_message() {
        var response = ApiResponse.error("Not found");
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).isEqualTo("Not found");
        assertThat(response.getData()).isNull();
    }
}
