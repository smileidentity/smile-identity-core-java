package smile.identity.core;

import smile.identity.core.keys.SignatureKey;

public class Signature {

    private final String partnerId;
    private final String apiKey;

    public Signature(String partnerId, String apiKey) {
        this.partnerId = partnerId;
        this.apiKey = apiKey;
    }

    public SignatureKey generateSignature() {
        return generateSignature(System.currentTimeMillis());
    }

    public SignatureKey generateSignature(Long timestamp){
        return new SignatureKey(timestamp, this.partnerId, this.apiKey);
    }

    public Boolean confirmSignature(Long timestamp, String signature){
        SignatureKey key = new SignatureKey(timestamp, this.partnerId, this.apiKey);
        return key.validSignature(signature);
    }
}