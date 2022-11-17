package smile.identity.core.models;


import lombok.Value;

@Value
public class WebTokenResponse {

    boolean success;
    String token;

}
