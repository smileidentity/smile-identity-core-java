package smile.identity.core.exceptions;

public class IdTypeNotSupported extends Exception {
    public IdTypeNotSupported(String country, String idType) {
        String.format("Country / Id Type combo is not supported: [%s - %s]",
                country, idType);
    }
}
