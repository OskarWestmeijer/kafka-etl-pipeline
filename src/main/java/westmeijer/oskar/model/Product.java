package westmeijer.oskar.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.io.Serializable;
import lombok.*;

@Builder
public record Product(@Positive
                      @NotNull
                      Integer id,

                      @NotEmpty(message = "Name cannot be empty.")
                      @NotNull(message = "Name cannot be null.")
                      String name) implements Serializable {

}
