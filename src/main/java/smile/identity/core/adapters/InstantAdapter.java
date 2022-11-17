package smile.identity.core.adapters;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;

import java.time.Instant;

public class InstantAdapter {

    @ToJson String toJson(Instant instant){
        return instant.toString();
    }

    @FromJson Instant fromJson(String string){
        return Instant.parse(string);
    }
}
