package smile.identity.core.models;

import com.squareup.moshi.Json;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class EnhancedKYCRequest {

    @Json(name = "partner_id")
    private String partnerId;

    private String timestamp;

    private String signature;

    @Json(name = "partner_params")
    private PartnerParams partnerParams;

    private String country;

    @Json(name = "first_name")
    private String firstName;

    @Json(name = "last_name")
    private String lastName;

    @Json(name = "id_type")
    private String idType;

    @Json(name = "id_number")
    private String idNumber;

    private String dob;

    @Json(name = "phone_number")
    private String phoneNumber;

    @Json(name = "image_links")
    private boolean imageLinks;

    private boolean history;

}
