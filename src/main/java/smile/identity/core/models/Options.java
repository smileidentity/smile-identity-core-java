package smile.identity.core.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class Options {
    private boolean returnHistory = false;
    private boolean returnImageLinks = false;
    private boolean signature = true;
    private boolean returnJobStatus = false;
    private String callbackUrl;

}