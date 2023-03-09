

public class Signature {

    public static main(String [] args) {
        // To confirm a signature returned back from the server you can either send the timestamp as a long

        String timestamp = request.getTimestamp();
        String signature = request.getSignature();

        boolean signatureConfirmed = signature.confirmSignature(timestamp, signature);

        // To generate a new signature without providing a timestamp

        SignatureKey signatureKey = signature.getSignatureKey();
        String timestamp = signatureKey.getTimestamp();
        String signature = signatureKey.getSignature();

        // To generate a new signature with an ISO String

        String timestamp = "2023-03-09T21:35:00.279Z";
        SignatureKey signatureKey = signature.getSignatureKey(timestamp);
        String signature = signatureKey.getSignature();
    }

}
