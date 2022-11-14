package smile.identity.core.models;

import com.squareup.moshi.Json;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Actions {

    @Json(name = "Verify_ID_Number")
    private String verifyIdNumber;

    @Json(name = "Return_Personal_Info")
    private String returnPersonalInfo;

    @Json(name = "Human_Review_Compare")
    private String humanReviewCompare;

    @Json(name = "Human_Review_Liveness_Check")
    private String humanReviewLivenessCheck;

    @Json(name = "Liveness_Check")
    private String livenessCheck;

    @Json(name = "Register_Selfie")
    private String registerSelfie;

    @Json(name = "Selfie_Provided")
    private String selfieProvided;

    @Json(name = "Selfie_To_ID_Authority_Compare")
    private String selfieToIdAuthorityCompare;

    @Json(name = "Selfie_To_ID_Card_Compare")
    private String selfieToIdCardCompare;

    @Json(name = "Selfie_To_Registered_Selfie_Compare")
    private String selfieToRegisteredSelfieCompare;

    @Json(name = "Update_Registered_Selfie_On_File")
    private String updateRegisteredSelfieOnFile;

}