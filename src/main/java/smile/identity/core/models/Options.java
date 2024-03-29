package smile.identity.core.models;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class Options {
    boolean returnHistory;
    boolean returnImageLinks;
    boolean returnJobStatus;
    String callbackUrl;

    public Options() {

        this(false, false, false, "");
    }

}
