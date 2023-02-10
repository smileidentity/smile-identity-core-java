

public class Signature {

    public static main(String [] args) {
        // To confirm a signature returned back from the server

        String timestamp = request.getTimeStamp();
        String signature = request.getSignature();

        Instant instant = Instant.parse(timestamp);
        boolean signatureConfirmed = signature.confirmSignature(instant.toEpochMilli());


    }

}
