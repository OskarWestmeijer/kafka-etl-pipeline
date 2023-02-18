package westmeijer.oskar;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class Product {

    @Positive
    @NonNull
    Integer id;
    @NotBlank
    @NonNull
    String name;

}
