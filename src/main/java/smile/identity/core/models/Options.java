package smile.identity.core.models;

import lombok.Value;

@Value
public class Options {
    boolean returnHistory = false;
    boolean returnImageLinks = false;
    boolean signature = true;
    boolean returnJobStatus = false;
    String callbackUrl;

}
