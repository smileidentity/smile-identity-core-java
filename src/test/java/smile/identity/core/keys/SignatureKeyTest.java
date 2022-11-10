package smile.identity.core.keys;

import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class SignatureKeyTest {

    @Test
    public void generateSignatureKey() {
        long timestamp = 1668090818630L;
        String signature = "KNDw4XBi92pINYzNu0Y4iXgXyTjDn2yoaoCg5z6pic0=";
        SignatureKey key = new SignatureKey(timestamp, "partner", "apikey");

        Assert.assertEquals(signature, key.getSignature());
        Assert.assertEquals(timestamp, key.getTimestamp());
    }

    @Test
    public void validateSignature() {
        long timestamp = System.currentTimeMillis();
        SignatureKey key = new SignatureKey(timestamp, "partner", "apikey");
        assert (key.validSignature(key.getSignature()));
    }

    @Test
    public void formatTimestamp() {
        long timestamp = System.currentTimeMillis();
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