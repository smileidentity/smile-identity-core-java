package smile.identity.core.models;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class UserData {
    boolean isVerifiedProcess;
    String name;
    String fbUserID;
    String firstName;
    String lastName;
    String gender;
    String email;
    String phone;
    String countryCode;
    String countryName;

    public UserData(){
        this(false, "", "", "", "", "", "", "", "+", "");
    }

}
