package smile.identity.core.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class UserData {
    private final boolean isVerifiedProcess = false;
    private String name;
    private String fbUserID;
    private final String firstName = "Bill";
    private String lastName;
    private String gender;
    private String email;
    private String phone;
    private final String countryCode = "+";
    private String countryName;

}
