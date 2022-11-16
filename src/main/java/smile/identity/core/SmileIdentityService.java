package smile.identity.core;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;
import smile.identity.core.adapters.JobTypeAdapter;
import smile.identity.core.adapters.InstantAdapter;
import smile.identity.core.adapters.PartnerParamsAdapter;
import smile.identity.core.exceptions.JobFailed;
import smile.identity.core.models.*;

import java.io.IOException;
import java.lang.reflect.Type;

public class SmileIdentityService {
    private final SmileIdentityApi smileIdentityApi;
    private final JsonAdapter<ErrorResponse> errorAdaptor = new Moshi.Builder().build().adapter(ErrorResponse.class);

    public SmileIdentityService(String server) {
        this(server,  new OkHttpClient.Builder());
    }

    public SmileIdentityService(String server, OkHttpClient.Builder httpClient){
        PolymorphicJsonAdapterFactory<JobResponse> factory = PolymorphicJsonAdapterFactory.of(JobResponse.class, "ResultType")
                .withSubtype(EnhancedResponse.class, "ID Verification")
                .withSubtype(EnhancedResponse.class, "Document Verification")
                .withFallbackJsonAdapter(new Moshi.Builder().add(new InstantAdapter()).build().adapter((Type) JobResponse.class));

        Moshi moshi = new Moshi.Builder()
                .add(factory)
                .add(new PartnerParamsAdapter())
                .add(new JobTypeAdapter())
                .add(new InstantAdapter())
                .build();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(server)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .client(httpClient.build())
                .build();
        this.smileIdentityApi = retrofit.create(SmileIdentityApi.class);
    }


    public String getServices() throws IOException, JobFailed {
        Call<ResponseBody> call = smileIdentityApi.getServices();
        Response<ResponseBody> response = call.execute();

        if (response.isSuccessful()){
            return response.body().string();
        } else {
            ErrorResponse error = errorAdaptor.fromJson(response.errorBody().string());
            throw new JobFailed(error.getError(), error.getCode());
        }
    }

    public JobResponse idVerification(EnhancedKYCRequest idVerificationRequest) throws IOException, JobFailed {
        Call<JobResponse> call = smileIdentityApi.submitIdVerification(idVerificationRequest);
        Response<JobResponse> response = call.execute();
        if(response.isSuccessful()){
            return response.body();
        } else {
            ErrorResponse error = errorAdaptor.fromJson(response.errorBody().string());
            throw new JobFailed(error.getError(), error.getCode());
        }
    }

    public JobStatusResponse getJobStatus(JobStatusRequest request) throws Exception {
        Call<JobStatusResponse> call = smileIdentityApi.getJobStatus(request);

        Response<JobStatusResponse> response = call.execute();
        if (response.isSuccessful()) {
            return response.body();
        } else {
            ErrorResponse error = errorAdaptor.fromJson(response.errorBody().string());
            throw new JobFailed(error.getError(), error.getCode());
        }
    }

    public JobStatusResponse pollJobStatus(JobStatusRequest request, int retryCount, long initialDelay) throws Exception {
        int count = 0;
        JobStatusResponse response = getJobStatus(request);
        while(!response.isJobComplete() && count <= retryCount ){
            long waitTime = ((long) Math.pow(2, count) * initialDelay);
            Thread.sleep(waitTime);
            response = getJobStatus(request);
            count++;
        }
        return response;
    }


    public PreUploadResponse preUpload(PreUploadRequest request) throws JobFailed, IOException{
        Call<PreUploadResponse> call = smileIdentityApi.prepUpload(request);
        Response<PreUploadResponse> response = call.execute();
        if (response.isSuccessful()) {
            return response.body();
        } else {
            ErrorResponse error = errorAdaptor.fromJson(response.errorBody().string());
            throw new JobFailed(error.getError(), error.getCode());
        }
    }

    public void uploadImages(String url, byte[] data) throws JobFailed, IOException{
        RequestBody body = RequestBody.create(MediaType.parse("application/zip"), data);
        Call<ResponseBody> call = smileIdentityApi.uploadBinaryFile(url, body);
        Response<ResponseBody> response = call.execute();
        if(!response.isSuccessful()){
            ErrorResponse error = errorAdaptor.fromJson(response.errorBody().string());
            throw new JobFailed(error.getError(), error.getCode());
        }
    }

    public WebTokenResponse getWebToken(WebTokenRequest request) throws Exception{
        Call<WebTokenResponse> call = smileIdentityApi.getWebToken(request);
        Response<WebTokenResponse> response = call.execute();
        return response.body();
    }

}
