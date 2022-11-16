package smile.identity.core.models;

import lombok.Value;

@Value
public class UserData {
    boolean isVerifiedProcess = false;
    String name;
    String fbUserID;
    String firstName = "Bill";
    String lastName;
    String gender;
    String email;
    String phone;
    String countryCode = "+";
    String countryName;

}
