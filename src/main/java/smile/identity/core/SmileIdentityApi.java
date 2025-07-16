package smile.identity.core;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Url;
import smile.identity.core.models.EnhancedKYCRequest;
import smile.identity.core.models.JobResponse;
import smile.identity.core.models.JobStatusRequest;
import smile.identity.core.models.JobStatusResponse;
import smile.identity.core.models.PreUploadRequest;
import smile.identity.core.models.PreUploadResponse;
import smile.identity.core.models.WebTokenRequest;
import smile.identity.core.models.WebTokenResponse;

public interface SmileIdentityApi {

    @GET("v1/services")
    Call<ResponseBody> getServices();

    @POST("v1/id_verification")
    Call<JobResponse> submitIdVerification(@Body EnhancedKYCRequest request);

    @POST("v1/job_status")
    Call<JobStatusResponse> getJobStatus(@Body JobStatusRequest request);

    @POST("v1/upload")
    Call<PreUploadResponse> prepUpload(@Body PreUploadRequest request);

    @POST("v1/token")
    Call<WebTokenResponse> getWebToken(@Body WebTokenRequest request);

    @PUT
    Call<ResponseBody> uploadBinaryFile(@Url String url, @Body RequestBody body);
}
