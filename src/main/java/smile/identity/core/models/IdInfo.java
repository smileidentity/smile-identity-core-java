package smile.identity.core.models;


import com.squareup.moshi.Json;

import lombok.Value;

@Value
public class IdInfo {
    @Json(name = "first_name")
    private String firstName;

    @Json(name = "middle_name")
    private String middleName;

    @Json(name = "last_name")
    private String lastName;

    private String country;

    @Json(name = "id_type")
    private String idType;

    @Json(name = "id_number")
    private String idNumber;

    private String dob;

    @Json(name = "phone_number")
    private String phoneNumber;

    public Boolean valid() {
        return (this.country != null && this.idType != null && this.idNumber != null);
    }
}
