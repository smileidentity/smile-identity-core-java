

public class Signature {

    public static main(String [] args) {
        // To confirm a signature returned back from the server you can either send the timestamp as a long

        String timestamp = request.getTimeStamp();
        String signature = request.getSignature();

        Instant instant = Instant.parse(timestamp);
        boolean signatureConfirmed = signature.confirmSignature(instant.toEpochMilli(), signature);

        // Or pass a string

        String timestamp = request.getTimeStamp();
        String signature = request.getSignature();

        boolean signatureConfirmed = signature.confirmSignature(timestamp, signature);

    }

}
