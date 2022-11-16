package smile.identity.core.models;

import com.squareup.moshi.Json;

import lombok.Value;


@Value
public class EnhancedKYCRequest {

    @Json(name = "partner_id")
    String partnerId;

    String timestamp;

    String signature;

    @Json(name = "partner_params")
    PartnerParams partnerParams;

    String country;

    @Json(name = "first_name")
    String firstName;

    @Json(name = "last_name")
    String lastName;

    @Json(name = "id_type")
    String idType;

    @Json(name = "id_number")
    String idNumber;

    String dob;

    @Json(name = "phone_number")
    String phoneNumber;

    @Json(name = "image_links")
    boolean imageLinks;

    boolean history;

}
