package smile.identity.core;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;
import smile.identity.core.models.*;

public interface SmileIdentityApi {

    @GET("services")
    Call<ResponseBody> getServices();

    @POST("id_verification")
    Call<JobResponse> submitIdVerification(@Body EnhancedKYCRequest request);

    @POST("job_status")
    Call<JobStatusResponse> getJobStatus(@Body JobStatusRequest request);

    @Headers("Content-Type: application/json")
    @POST("upload")
    Call<PreUploadResponse> prepUpload(@Body PreUploadRequest request);

    @POST("token")
    Call<WebTokenResponse> getWebToken(@Body WebTokenRequest request);

    @PUT
    Call<ResponseBody> uploadBinaryFile(@Url String url, @Body RequestBody body);
}
