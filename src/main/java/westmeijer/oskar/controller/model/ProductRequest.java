package westmeijer.oskar.controller.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ProductRequest(@Positive
                             @NotNull
                             Integer id,
                             @NotEmpty(message = "Name cannot be empty.")
                             @NotNull(message = "Name cannot be null.")
                             String name) {

}
