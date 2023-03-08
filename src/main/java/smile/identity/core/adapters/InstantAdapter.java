package smile.identity.core.adapters;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;

import java.time.DateTimeException;
import java.time.Instant;

public class InstantAdapter {

    @ToJson
    String toJson(Instant instant) {
        return instant.toString();
    }

    @FromJson
    Instant fromJson(String string) {
        Instant timestamp;
        try {
            timestamp = Instant.ofEpochMilli(Long.parseLong(string));
        } catch (DateTimeException | NumberFormatException ex) {
            timestamp = Instant.parse(string);
        }
        return timestamp;
    }
}
