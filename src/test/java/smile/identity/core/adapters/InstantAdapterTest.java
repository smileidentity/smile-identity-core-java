package smile.identity.core.adapters;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import org.junit.Test;
import smile.identity.core.MoshiUtils;

import java.time.Instant;

import static org.junit.Assert.assertEquals;

public class InstantAdapterTest {

    Moshi moshi = MoshiUtils.getMoshi();
    @Test
    public void testFromJsonLong() {
        long timestamp = 1678334303;
        Instant actual = Instant.parse("1970-01-20T10:12:14.303Z");
        JsonAdapter<Instant> adapter = moshi.adapter(Instant.class);
        Instant instant = adapter.fromJsonValue(timestamp);
        assertEquals(actual, instant);
    }

    @Test
    public void testFromJsonString() {
        String timestamp = "1970-01-20T10:12:14.303Z";
        Instant actual = Instant.parse("1970-01-20T10:12:14.303Z");
        JsonAdapter<Instant> adapter = moshi.adapter(Instant.class);
        Instant instant = adapter.fromJsonValue(timestamp);
        assertEquals(actual, instant);
    }


}
