package smile.identity.core.keys;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.util.Base64;

public class SignatureKey {

    private final long timestamp;
    private final String signature;

    public SignatureKey(long timestamp, String partnerId, String apiKey) {
        this.timestamp = timestamp;
        this.signature = generate(partnerId, apiKey);
    }

    private String generate(String partnerId, String apiKey) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(apiKey.getBytes(), "HmacSHA256"));
            mac.update(getInstant().toString().getBytes(StandardCharsets.UTF_8));
            mac.update(partnerId.getBytes(StandardCharsets.UTF_8));
            mac.update("sid_request".getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(mac.doFinal());
        } catch (GeneralSecurityException e){
            throw new RuntimeException(e);
        }
    }

    public String getSignature(){
        return this.signature;
    }

    public long getTimestamp(){
        return this.timestamp;
    }

    public boolean validSignature(String signature) {
        return signature.equals(this.signature);
    }

    public Instant getInstant(){
        return Instant.ofEpochMilli(this.timestamp);
    }

}