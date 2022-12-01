package smile.identity.core;

//export package -tbd
//package com.smileidentity.services.WebApi

import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import smile.identity.core.adapters.ImageTypeAdapter;
import smile.identity.core.adapters.InstantAdapter;
import smile.identity.core.adapters.JobTypeAdapter;
import smile.identity.core.adapters.PartnerParamsAdapter;
import smile.identity.core.enums.ImageType;
import smile.identity.core.enums.JobType;
import smile.identity.core.enums.Product;
import smile.identity.core.exceptions.InvalidImageDetails;
import smile.identity.core.keys.SignatureKey;
import smile.identity.core.models.IdInfo;
import smile.identity.core.models.ImageDetail;
import smile.identity.core.models.JobResponse;
import smile.identity.core.models.JobStatusResponse;
import smile.identity.core.models.MiscInformation;
import smile.identity.core.models.Options;
import smile.identity.core.models.PackageInformation;
import smile.identity.core.models.PartnerParams;
import smile.identity.core.models.PreUploadRequest;
import smile.identity.core.models.PreUploadResponse;
import smile.identity.core.models.UploadRequest;
import smile.identity.core.models.UserData;
import smile.identity.core.models.WebTokenRequest;
import smile.identity.core.models.WebTokenResponse;


public class WebApi {

    private static final List<String> SUPPORTED_IMAGE_TYPES = Arrays.asList(
            "png", "jpg", "jpeg");

    private final String partnerId;
    private final String apiKey;

    private final String sidServer;
    private final String defaultCallbackUrl;
    private final SmileIdentityService smileIdentityService;

    public WebApi(String partnerId, String apiKey, String defaultCallback,
                  String sidServer) {
        this.partnerId = partnerId;
        this.defaultCallbackUrl = defaultCallback;
        this.apiKey = apiKey;
        this.sidServer = ConfigHelpers.getSidServer(sidServer);
        this.smileIdentityService = new SmileIdentityService(sidServer);
    }


    public JobStatusResponse submitJob(PartnerParams partnerParams,
                                       IdInfo idInfo) throws Exception {
        return submitJob(partnerParams, new ArrayList<>(), idInfo,
                new Options());
    }

    public JobStatusResponse submitJob(PartnerParams partnerParams,
                                       List<ImageDetail> imageDetails,
                                       IdInfo idInfo, Options options) throws Exception {
        return submitJob(partnerParams, imageDetails, idInfo, options, true);
    }

    public JobStatusResponse submitJob(PartnerParams partnerParams,
                                       List<ImageDetail> imageDetails,
                                       IdInfo idInfo, Options options,
                                       boolean useValidationApi) throws Exception {

        JobType jobType = partnerParams.getJobType();
        String callbackUrl = getCallbackUrl(options.getCallbackUrl());

        if (jobType.isKYC()) {
            return new IDApi(this.partnerId, this.apiKey, this.sidServer).submitJob(partnerParams, idInfo, options);
        }

        verifyJobReturnMethod(callbackUrl, options.isReturnJobStatus());
        verifySelfieIsPresent(imageDetails);

        if (jobType.equals(JobType.BIOMETRIC_KYC)) {
            verifyIdIsPresent(idInfo, imageDetails);
        }

        if (jobType.equals(JobType.BIOMETRIC_KYC) && useValidationApi) {
            IdValidator.validateIdType(smileIdentityService, idInfo,
                    partnerParams);
        }

        PreUploadRequest preUploadRequest =
                configurePreUploadRequest(partnerParams, callbackUrl);
        PreUploadResponse uploadResponse =
                smileIdentityService.preUpload(preUploadRequest);

        String uploadUrl = uploadResponse.getUploadUrl();

        UploadRequest infoJson = configureUploadRequest(uploadResponse,
                idInfo, preUploadRequest, imageDetails);

        byte[] uploadData = zipUpFile(infoJson, imageDetails);
        smileIdentityService.uploadImages(uploadUrl, uploadData);
        if (options.isReturnJobStatus()) {
            return new Utilities(this.partnerId, this.apiKey, sidServer).pollJobStatus(partnerParams.getUserId(), partnerParams.getJobId());
        } else {
            JobResponse result = new JobResponse("",
                    uploadResponse.getSmileJobId(), partnerParams, "", "", ""
                    , "", null, "", null, "", "", null);
            return new JobStatusResponse("", false, true, result, "", null,
                    null, null);
        }
    }


    public String getWebToken(Long timestamp, String userId, String jobId,
                              Product product) throws Exception {
        return getWebToken(timestamp, userId, jobId, product,
                this.defaultCallbackUrl);

    }

    public String getWebToken(Long timestamp, String userId, String jobId,
                              Product product, String callbackUrl) throws Exception {

        SignatureKey key =
                new Signature(this.partnerId, this.apiKey).getSignatureKey(timestamp);

        WebTokenRequest request = new WebTokenRequest(userId, jobId, product,
                callbackUrl, key.getSignature(), key.getInstant(),
                (this.partnerId));

        WebTokenResponse response = smileIdentityService.getWebToken(request);
        return response.getToken();
    }


    private void verifySelfieIsPresent(List<ImageDetail> imageDetails) throws Exception {
        if (imageDetails == null || imageDetails.isEmpty()) {
            throw new InvalidImageDetails("You need to send through at least "
                    + "one selfie image");
        }

        Optional<ImageDetail> selfieDetail =
                imageDetails.stream().parallel().filter(details -> details.getImageTypeId().equals(ImageType.SELFIE) || details.getImageTypeId().equals(ImageType.SELFIE_BASE64)).findAny();
        if (!selfieDetail.isPresent()) {
            throw new InvalidImageDetails("You need to send through at least "
                    + "one selfie image");
        }
    }

    private void verifyIdIsPresent(IdInfo idInfo,
                                   List<ImageDetail> imageDetails) throws Exception {
        Optional<ImageDetail> idCard =
                imageDetails.stream().parallel().filter(details -> details.getImageTypeId().equals(ImageType.ID_CARD) || details.getImageTypeId().equals(ImageType.ID_CARD_BASE64)).findAny();
        if (!idCard.isPresent() && (idInfo == null || !idInfo.valid())) {
            throw new InvalidImageDetails("You are attempting to complete a " + "job type 1 without providing an id card image or id info");
        }
    }

    private void verifyJobReturnMethod(String callbackUrl,
                                       boolean returnJobStatus) {
        if (Strings.isNullOrEmpty(callbackUrl) && !returnJobStatus) {
            throw new IllegalArgumentException("Please choose to either get " + "your response via the callback or job status query");
        }
    }


    private PreUploadRequest configurePreUploadRequest(PartnerParams partnerParams, String callbackUrl) {
        Signature signature = new Signature(this.partnerId, this.apiKey);
        SignatureKey key = signature.getSignatureKey();
        return new PreUploadRequest(key.getInstant(), key.getSignature(),
                this.partnerId, partnerParams, callbackUrl);
    }


    private UploadRequest configureUploadRequest(PreUploadResponse uploadResponse, IdInfo idInfo, PreUploadRequest uploadRequest, List<ImageDetail> imageDetails) {

        Signature signature = new Signature(this.partnerId, this.apiKey);
        SignatureKey key = signature.getSignatureKey();

        UserData userData = new UserData();

        List<ImageDetail> updatedImageDetails = new ArrayList<>();
        for (ImageDetail imageDetail : imageDetails) {
            if (!StringUtils.isEmpty(imageDetail.getFileName())) {
                Path path = Paths.get(imageDetail.getFileName());
                updatedImageDetails.add(new ImageDetail(imageDetail.getImageTypeId(), "", path.getFileName().toString()));
            } else {
                updatedImageDetails.add(new ImageDetail(imageDetail.getImageTypeId(), imageDetail.getImage(), ""));
            }
        }

        MiscInformation miscInformation =
                new MiscInformation(uploadRequest.getPartnerParams(),
                        key.getInstant(), key.getSignature(), this.partnerId,
                        uploadRequest.getCallbackUrl(), userData);

        return new UploadRequest(new PackageInformation(), miscInformation,
                idInfo, updatedImageDetails, uploadResponse);
    }


    private byte[] zipUpFile(UploadRequest infoJson,
                             List<ImageDetail> imageDetails) throws Exception {
        // http://www.avajava.com/tutorials/lessons/how-can-i-create-a-zip-file-from-a-set-of-files.html
        // https://stackoverflow.com/questions/23612864/create-a-zip-file-in-memory

        Moshi moshi =
                new Moshi.Builder().add(new JobTypeAdapter()).add(new InstantAdapter()).add(new ImageTypeAdapter()).add(new PartnerParamsAdapter()).build();

        JsonAdapter<UploadRequest> jsonAdapter =
                moshi.adapter(UploadRequest.class);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);

        ZipEntry entry = new ZipEntry("info.json");
        String infoJsonString = jsonAdapter.toJson(infoJson);
        zos.putNextEntry(entry);
        zos.write(infoJsonString.getBytes());
        zos.closeEntry();

        for (ImageDetail imageDetail : imageDetails) {
            if (imageDetail.getFileName() != null && !imageDetail.getFileName().isEmpty() && SUPPORTED_IMAGE_TYPES.contains(Files.getFileExtension(imageDetail.getFileName()))) {
                File file = new File(imageDetail.getFileName());
                FileInputStream fis = new FileInputStream(file);
                ZipEntry imageEntry = new ZipEntry(file.getName());
                zos.putNextEntry(imageEntry);

                byte[] bytes = new byte[1024];
                int length;
                while ((length = fis.read(bytes)) >= 0) {
                    zos.write(bytes, 0, length);
                }
                zos.closeEntry();
                fis.close();
            }
        }
        zos.flush();
        zos.close();
        baos.close();
        return baos.toByteArray();
    }


    private String getCallbackUrl(String callbackUrl) {
        if (!Strings.isNullOrEmpty(callbackUrl)) {
            return callbackUrl;
        }
        return this.defaultCallbackUrl;
    }
}