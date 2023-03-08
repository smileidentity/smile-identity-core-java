package smile.identity.core.adapters;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.ToJson;
import smile.identity.core.SmileIdentityMoshi;
import smile.identity.core.models.JobResponse;
import smile.identity.core.models.Result;


/**
 *  The job_status endpoint occasionally sends back a
 *  String response. This Adapter first tries to convert the
 *  result to a JobResponse object, if it fails it returns the
 *  response as a String.
 */
public class ResultAdapter {

    @ToJson Object toJson(Result result) {
        if (result.getMessage() != null){
            return result.getMessage();
        } else {
            return result.getJobResponse();
        }
    }

    @FromJson
    Result fromJson(Object result) {
        try {
            Moshi moshi = SmileIdentityMoshi.getMoshi();
            JsonAdapter<JobResponse> adapter = moshi.adapter(JobResponse.class);
            JobResponse jobResponse = adapter.fromJsonValue(result);
            return new Result(null, jobResponse);
        } catch (Exception ex) {
            return new Result(result.toString(), null);
        }
    }
}
