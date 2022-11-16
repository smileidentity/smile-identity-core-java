package smile.identity.core.models;

import com.squareup.moshi.Json;
import lombok.Value;

@Value
public class Actions {

    @Json(name = "Verify_ID_Number")
    String verifyIdNumber;

    @Json(name = "Return_Personal_Info")
    String returnPersonalInfo;

    @Json(name = "Human_Review_Compare")
    String humanReviewCompare;

    @Json(name = "Human_Review_Liveness_Check")
    String humanReviewLivenessCheck;

    @Json(name = "Liveness_Check")
    String livenessCheck;

    @Json(name = "Register_Selfie")
    String registerSelfie;

    @Json(name = "Selfie_Provided")
    String selfieProvided;

    @Json(name = "Selfie_To_ID_Authority_Compare")
    String selfieToIdAuthorityCompare;

    @Json(name = "Selfie_To_ID_Card_Compare")
    String selfieToIdCardCompare;

    @Json(name = "Selfie_To_Registered_Selfie_Compare")
    String selfieToRegisteredSelfieCompare;

    @Json(name = "Update_Registered_Selfie_On_File")
    String updateRegisteredSelfieOnFile;

}
