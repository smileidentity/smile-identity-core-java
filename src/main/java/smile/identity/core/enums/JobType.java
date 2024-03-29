package smile.identity.core.enums;

public enum JobType {
    BIOMETRIC_KYC(1),
    SMART_SELFIE_REGISTRATION(4),
    SMART_SELFIE_AUTHENTICATION(2),
    BASIC_KYC(5),
    ENHANCED_KYC(5),
    DOCUMENT_VERIFICATION(6),
    BUSINESS_VERIFICATION(7);

    private final int value;
    JobType(int value){
        this.value = value;
    }

    public int getValue(){
        return this.value;
    }

    public static JobType fromValue(int value){
        for (JobType s : values()) {
            if (s.value == value) {
                return s;
            }
        }
        return null;
    }

    public boolean isKYC() {
        return this.value == 5;
    }

}
