package smile.identity.core.models;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class Result {
    String message;

    JobResponse jobResponse;

}
