package smile.identity.core.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class WebTokenResponse {

    private boolean success;
    private String token;

}