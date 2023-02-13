package smile.identity.core;

import smile.identity.core.keys.SignatureKey;

import java.time.Instant;

public class Signature {

    private final String partnerId;
    private final String apiKey;

    public Signature(String partnerId, String apiKey) {
        this.partnerId = partnerId;
        this.apiKey = apiKey;
    }

    public SignatureKey getSignatureKey() {
        return getSignatureKey(System.currentTimeMillis());
    }

    public SignatureKey getSignatureKey(long timestamp){
        return new SignatureKey(timestamp, this.partnerId, this.apiKey);
    }

    public boolean confirmSignature(long timestamp, String signature){
        SignatureKey key = new SignatureKey(timestamp, this.partnerId, this.apiKey);
        return key.validSignature(signature);
    }

    public boolean confirmSignature(String timestamp, String signature) {
        Instant instant = Instant.parse(timestamp);
        SignatureKey key = new SignatureKey(instant.toEpochMilli(), this.partnerId, this.apiKey);
        return key.validSignature(signature);
    }
}