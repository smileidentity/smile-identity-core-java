package smile.identity.core.adapters;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;
import smile.identity.core.enums.JobType;

public class JobTypeAdapter {
    @ToJson int toJson(JobType jobType){
        return jobType.getValue();
    }

    @FromJson JobType fromJson(int value) {
        return JobType.fromValue(value);
    }
}
