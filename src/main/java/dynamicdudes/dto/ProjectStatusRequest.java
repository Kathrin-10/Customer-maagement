package dynamicdudes.dto;

import jakarta.validation.constraints.NotBlank;

public record ProjectStatusRequest(@NotBlank String status) {
}
