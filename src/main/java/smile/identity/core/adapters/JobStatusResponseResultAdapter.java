package smile.identity.core.adapters;

import com.squareup.moshi.*;
import smile.identity.core.models.JobResponse;
import smile.identity.core.models.JobStatusResponse;

import java.io.IOException;


/**
 * The job_status endpoint occasionally sends back a
 * String response. This Adapter first tries to convert the
 * result to a JobResponse object, if it fails it returns the
 * response as a String.
 */
public class JobStatusResponseResultAdapter {

    @ToJson
    Object toJson(JobStatusResponse.Result result) {
        if (result.getMessage() != null) {
            return result.getMessage();
        } else {
            return result.getJobResponse();
        }
    }

    @FromJson
    JobStatusResponse.Result fromJson(JsonReader jsonReader,
                                      JsonAdapter<JobResponse> jobResponseDelegate) throws IOException {
        try {
            JobResponse jobResponse = jobResponseDelegate.fromJson(jsonReader);
            return new JobStatusResponse.Result(jobResponse);
        } catch (Exception ex) {
            Object object = jsonReader.readJsonValue();
            if (object != null) {
                return new JobStatusResponse.Result(object.toString());
            }
            return new JobStatusResponse.Result();
        }
    }
}
