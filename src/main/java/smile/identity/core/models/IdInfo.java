package smile.identity.core.models;


import com.google.common.base.Strings;
import com.squareup.moshi.Json;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class IdInfo {
    @Json(name = "first_name")
    String firstName;

    @Json(name = "middle_name")
    String middleName;

    @Json(name = "last_name")
    String lastName;

    String country;

    @Json(name = "id_type")
    String idType;

    @Json(name = "id_number")
    String idNumber;

    String dob;

    @Json(name = "phone_number")
    String phoneNumber;

    @Json(name = "business_type")
    String businessType;

    @Json(name = "postal_address")
    String postalAddress;

    @Json(name = "postal_code")
    String postalCode;
    boolean entered = true;

    public IdInfo(String country, String idType, String idNumber,
                  String businessType, String postalAddress,
                  String postalCode) {
        this("", "", "", country, idType, idNumber, "", "", businessType,
                postalAddress, postalCode);
    }

    public IdInfo(String firstName, String middleName, String lastName,
                  String country, String idType, String idNumber, String dob,
                  String phoneNumber) {
        this(firstName, middleName, lastName, country, idType, idNumber, dob,
                phoneNumber, "", "", "");
    }

    public IdInfo(String country, String idType) {
        this("", "", "", country, idType, "", "", "", "",
        "", "");
    }

    public IdInfo(String country, String idType, String idNumber) {
        this("", "", "", country, idType, idNumber, "", "", "", "", "");
    }

    public Boolean valid() {
        return (!Strings.isNullOrEmpty(country) && !Strings.isNullOrEmpty(this.idType) && !Strings.isNullOrEmpty(this.idNumber));
    }
}
