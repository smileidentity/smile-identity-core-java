package smile.identity.core.models;

import com.squareup.moshi.Json;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
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
}
