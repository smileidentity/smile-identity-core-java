package smile.identity.core.keys;

import org.junit.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static org.junit.Assert.*;

public class SignatureKeyTest {

    @Test
    public void generateSignatureKey() {
        String timestamp = "2023-03-09T21:35:00.279Z";
        String signature = "+S/hVtmFwynoroXQQbjOQ3/l+a5zshNldrQh/iBdxRQ=";
        SignatureKey key = new SignatureKey(timestamp, "partner", "apikey");

        assertEquals(signature, key.getSignature());
        assertEquals(timestamp, key.getTimestamp());
    }

    @Test
    public void validateSignature() {
        String timestamp = "2023-03-09T21:35:00.279Z";
        String signature = "+S/hVtmFwynoroXQQbjOQ3/l+a5zshNldrQh/iBdxRQ=";
        SignatureKey key = new SignatureKey(timestamp, "partner", "apikey");
        assertTrue(key.validSignature(signature));
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