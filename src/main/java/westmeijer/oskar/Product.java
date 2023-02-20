package westmeijer.oskar;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Builder
public record Product(@Positive Integer id, @NotEmpty String name) {

}
