package smile.identity.core.models;

import com.squareup.moshi.Json;

import java.util.Map;

import lombok.Value;

@Value
public class EnhancedResponse extends JobResponse {
    @Json(name = "Country")
    private String country;

    @Json(name = "IDType")
    private String idType;

    @Json(name = "IDNumber")
    private String idNumber;

    @Json(name = "ExpirationDate")
    private String expirationDate;

    @Json(name = "FullName")
    private String fullName;

    @Json(name = "DOB")
    private String dob;

    @Json(name = "Photo")
    private String photo;

    @Json(name = "PhoneNumber")
    private String phoneNumber;

    @Json(name = "PhoneNumber2")
    private String phoneNumber2;

    @Json(name = "Document")
    private String document;

    @Json(name = "Gender")
    private String gender;

    @Json(name = "Address")
    private String address;


    public EnhancedResponse(String jsonVersion, String smileJobId, PartnerParams partnerParams,
                            String resultType, String resultText, String resultCode, String isFinalResult,
                            Actions actions, String signature, String timestamp, String confidence,
                            String source, Map<String, Object> fullData, String country, String idType,
                            String idNumber, String expirationDate, String fullName, String dob,
                            String photo, String phoneNumber, String phoneNumber2, String document,
                            String gender, String address) {
        super(jsonVersion, smileJobId, partnerParams, resultType, resultText, resultCode, isFinalResult,
                actions, signature, timestamp, confidence, source, fullData);
        this.country = country;
        this.idType = idType;
        this.idNumber = idNumber;
        this.expirationDate = expirationDate;
        this.fullName = fullName;
        this.dob = dob;
        this.photo = photo;
        this.phoneNumber = phoneNumber;
        this.phoneNumber2 = phoneNumber2;
        this.document = document;
        this.gender = gender;
        this.address = address;
    }
}
