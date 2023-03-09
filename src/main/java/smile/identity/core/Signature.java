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

    /**
     * Generates a SignatureKey
     *
     * @return SignatureKey
     */
    public SignatureKey getSignatureKey() {
        Instant instant = Instant.now();
        return getSignatureKey(instant.toString());
    }

    /**
     * Generates a SignatureKey with an ISO String
     *
     * @param timestamp ISO formatted timestamp
     * @return generated SignatureKey
     */
    public SignatureKey getSignatureKey(String timestamp) {
        return new SignatureKey(timestamp, this.partnerId, this.apiKey);
    }

    /**
     * Verifies the validity of the provided signature
     *
     * @param timestamp ISO formatted timestamp
     * @param signature Signature to verify
     * @return if signature is valid returns true. returns false if Signature is not valid
     */
    public boolean confirmSignature(String timestamp, String signature) {
        SignatureKey key = new SignatureKey(timestamp, this.partnerId, this.apiKey);
        return key.validSignature(signature);
    }
}
