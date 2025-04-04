package smile.identity.core;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;
import smile.identity.core.exceptions.JobFailed;
import smile.identity.core.models.*;

import java.io.IOException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class SmileIdentityService {
    public final SmileIdentityApi smileIdentityApi;
    private final JsonAdapter<ErrorResponse> errorAdaptor = new Moshi.Builder().build().adapter(ErrorResponse.class);
    static Logger logger = LogManager.getLogger(SmileIdentityService.class);

    public SmileIdentityService(String server) {
        this(server, new OkHttpClient.Builder()
                .callTimeout(120, java.util.concurrent.TimeUnit.SECONDS)
                .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        );
    }

    public SmileIdentityService(String server, OkHttpClient.Builder httpClient) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(server)
                .addConverterFactory(MoshiConverterFactory.create(MoshiUtils.getMoshi()))
                .client(httpClient.build())
                .build();
        this.smileIdentityApi = retrofit.create(SmileIdentityApi.class);
    }


    public String getServices() throws IOException, JobFailed {
        Call<ResponseBody> call = smileIdentityApi.getServices();
        Response<ResponseBody> response = call.execute();

        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            ErrorResponse error = errorAdaptor.fromJson(response.errorBody().string());
            throw new JobFailed(error.getError(), error.getCode());
        }
    }

    public JobResponse idVerification(EnhancedKYCRequest idVerificationRequest) throws IOException, JobFailed {
        Call<JobResponse> call = smileIdentityApi.submitIdVerification(idVerificationRequest);
        Response<JobResponse> response = call.execute();
        if (!response.isSuccessful()) {
            handleError(response);
        }
        return response.body();
    }

    public JobStatusResponse getJobStatus(JobStatusRequest request) throws Exception {
        Call<JobStatusResponse> call = smileIdentityApi.getJobStatus(request);

        Response<JobStatusResponse> response = call.execute();
        if (!response.isSuccessful()) {
            handleError(response);
        }
        return response.body();
    }

    public JobStatusResponse pollJobStatus(JobStatusRequest request, int retryCount, long initialDelay) throws Exception {
        int count = 0;
        JobStatusResponse response = getJobStatus(request);
        while (!response.isJobComplete() && count <= retryCount) {
            long waitTime = ((long) Math.pow(2, count) * initialDelay);
            Thread.sleep(waitTime);
            response = getJobStatus(request);
            count++;
        }
        return response;
    }


    public PreUploadResponse preUpload(PreUploadRequest request) throws JobFailed, IOException {
        Call<PreUploadResponse> call = smileIdentityApi.prepUpload(request);
        Response<PreUploadResponse> response = call.execute();
        if (!response.isSuccessful()) {
            handleError(response);
        }
        return response.body();
    }

    public void uploadImages(String url, byte[] data) throws JobFailed, IOException {
        RequestBody body = RequestBody.create(MediaType.parse("application/zip"), data);
        Call<ResponseBody> call = smileIdentityApi.uploadBinaryFile(url, body);
        Response<ResponseBody> response = call.execute();
        if (!response.isSuccessful()) {
            handleError(response);
        }
    }

    public WebTokenResponse getWebToken(WebTokenRequest request) throws Exception {
        Call<WebTokenResponse> call = smileIdentityApi.getWebToken(request);
        Response<WebTokenResponse> response = call.execute();
        if (!response.isSuccessful()) {
            handleError(response);
        }
        return response.body();
    }

    private void handleError(Response<?> response) throws IOException, JobFailed {
        ErrorResponse error = errorAdaptor.fromJson(response.errorBody().string());
        logger.error(String.format("Response from server: Code %s => %s", error.getCode(), error.getError()));
        throw new JobFailed(error.getError(), error.getCode());
    }

}
