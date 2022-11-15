package smile.identity.core;

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
import smile.identity.core.adaptors.JobTypeAdaptor;
import smile.identity.core.adaptors.PartnerParamsAdaptor;
import smile.identity.core.exceptions.JobFailed;
import smile.identity.core.models.*;

import java.io.IOException;
import java.lang.reflect.Type;

public class SmileService {
    private final SmileApi service;

    public SmileService(String server){

        PolymorphicJsonAdapterFactory<JobResponse> factory = PolymorphicJsonAdapterFactory.of(JobResponse.class, "ResultType")
                .withSubtype(EnhancedResponse.class, "ID Verification")
                .withSubtype(EnhancedResponse.class, "Document Verification")
                .withFallbackJsonAdapter(new Moshi.Builder().build().adapter((Type) JobResponse.class));

        Moshi moshi = new Moshi.Builder()
                .add(factory)
                .add(new PartnerParamsAdaptor())
                .add(new JobTypeAdaptor())
                .build();

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(server)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .client(httpClient.build())
                .build();
        this.service = retrofit.create(SmileApi.class);
    }


    public String getServices() throws IOException, JobFailed {
        Call<ResponseBody> call = service.getServices();
        Response<ResponseBody> response = call.execute();

        if (response.isSuccessful()){
            return response.body().string();
        } else {
            throw new JobFailed(response.errorBody().string());
        }
    }

    public JobResponse idVerification(EnhancedKYCRequest idVerificationRequest) throws IOException, JobFailed {
        Call<JobResponse> call = service.submitIdVerification(idVerificationRequest);
        Response<JobResponse> response = call.execute();
        if(response.isSuccessful()){
            return response.body();
        } else {
            throw new JobFailed(response.errorBody().string());
        }
    }

    public JobStatusResponse getJobStatus(JobStatusRequest request) throws Exception {
        Call<JobStatusResponse> call = service.getJobStatus(request);

        Response<JobStatusResponse> response = call.execute();
        if (response.isSuccessful()) {
            return response.body();
        } else {
            throw new JobFailed(response.errorBody().string());
        }
    }

    public JobStatusResponse pollJobStatus(JobStatusRequest request) throws Exception {
        int count = 0;
        JobStatusResponse response = getJobStatus(request);
        while(!response.isJobComplete() && count < 10 ){
            if(count < 4){
                Thread.sleep(2000);
            }else {
                Thread.sleep(4000);
            }
            response = getJobStatus(request);
            count++;
        }
        return response;
    }


    public PreUploadResponse preUpload(PreUploadRequest request) throws JobFailed, IOException{
        Call<PreUploadResponse> call = service.prepUpload(request);
        Response<PreUploadResponse> response = call.execute();
        if (response.isSuccessful()) {
            return response.body();
        } else {
            throw new JobFailed(response.errorBody().string());
        }
    }

    public void uploadImages(String url, byte[] data) throws JobFailed, IOException{
        RequestBody body = RequestBody.create(data, MediaType.parse("application/zip"));
        Call<ResponseBody> call = service.uploadBinaryFile(url, body);
        Response<ResponseBody> response = call.execute();
        if(!response.isSuccessful()){
            throw new JobFailed(response.errorBody().toString());
        }
    }

    public WebTokenResponse getWebToken(WebTokenRequest request) throws Exception{
        Call<WebTokenResponse> call = service.getWebToken(request);
        Response<WebTokenResponse> response = call.execute();
        return response.body();
    }

}
