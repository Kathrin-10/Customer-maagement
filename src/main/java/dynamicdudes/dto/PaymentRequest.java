package dynamicdudes.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record PaymentRequest(
        @NotNull Long projectId,
        @NotNull @DecimalMin("0.01") BigDecimal amount) {
}
