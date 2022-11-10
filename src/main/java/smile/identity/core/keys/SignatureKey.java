package smile.identity.core.keys;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.util.Base64;

public class SignatureKey {

    private Long timestamp;
    private String signature;

    public SignatureKey(Long timestamp, String partnerId, String apiKey) {
        this.timestamp = timestamp;
        this.signature = generateKey(partnerId, apiKey);
    }

    private String generateKey(String partnerId, String apiKey) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(apiKey.getBytes(), "HmacSHA256"));
            mac.update(formattedTimestamp().getBytes(StandardCharsets.UTF_8));
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

    public Long getTimestamp(){
        return this.timestamp;
    }

    public Boolean validSignature(String signature) {
        return signature.equals(this.signature);
    }

    public String formattedTimestamp(){
        return Instant.ofEpochMilli(this.timestamp).toString();
    }

}