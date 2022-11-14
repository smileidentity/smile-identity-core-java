package smile.identity.core;

import smile.identity.core.keys.SignatureKey;

public class Signature {

    private final String partnerId;
    private final String apiKey;

    public Signature(String partnerId, String apiKey) {
        this.partnerId = partnerId;
        this.apiKey = apiKey;
    }

    public SignatureKey getSignature() {
        return getSignature(System.currentTimeMillis());
    }

    public SignatureKey getSignature(long timestamp){
        return new SignatureKey(timestamp, this.partnerId, this.apiKey);
    }

    public boolean confirmSignature(long timestamp, String signature){
        SignatureKey key = new SignatureKey(timestamp, this.partnerId, this.apiKey);
        return key.validSignature(signature);
    }
}