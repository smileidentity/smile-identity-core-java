package smile.identity.core.enums;

import com.squareup.moshi.Json;

public enum Product {
    @Json(name = "authentication")
    AUTHENTICATION,
    @Json(name = "basic_kyc")
    BASIC_KYC,
    @Json(name = "smartselfie")
    SMARTSELFIE,
    @Json(name = "biometric_kyc")
    BIOMETRIC_KYC,
    @Json(name = "enhanced_kyc")
    ENHANCED_KYC,
    @Json(name = "doc_verification")
    DOC_VERIFICATION,
    @Json(name = "ekyc_smartselfie")
    EKYC_SMART_SEFLIE,
    @Json(name = "identity_verification")
    IDENTITY_VERIFICATION,
}
