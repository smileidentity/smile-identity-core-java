package smile.identity.core.models;

import lombok.Value;

@Value
public class ErrorResponse {
    String error;
    String code;
}
