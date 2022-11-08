package smile.identity.core.keys;

import org.junit.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class SignatureKeyTest {

    @Test
    public void generateSignatureKey() {
        Long timestamp = System.currentTimeMillis();
        SignatureKey key = new SignatureKey(timestamp, "partner", "apikey");
        assert (key.getSignature() != null);
        assert (key.getTimestamp() != null);
    }

    @Test
    public void validateSignature() {
        Long timestamp = System.currentTimeMillis();
        SignatureKey key = new SignatureKey(timestamp, "partner", "apikey");
        assert (key.validSignature(key.getSignature()));
    }

    @Test
    public void formatTimestamp() {
        Long timestamp = System.currentTimeMillis();
        SignatureKey key = new SignatureKey(timestamp, "partner", "apikey");
        String timestampString = key.formattedTimestamp();
        assert (validFormat(timestampString));
    }

    private boolean validFormat(String timestamp) {
        try {
            LocalDate.parse(timestamp, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        } catch (DateTimeParseException ex) {
            return false;
        }
        return true;
    }
}