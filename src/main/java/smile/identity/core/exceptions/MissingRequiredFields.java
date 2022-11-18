package smile.identity.core.exceptions;

public class MissingRequiredFields extends Exception {

    public MissingRequiredFields(String field) {
        super(String.format("Missing required field(s): %s",field));
    }
}
