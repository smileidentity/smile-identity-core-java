package smile.identity.core.enums;

public enum ImageType {
    SELFIE(0),
    ID_CARD(1),
    SELFIE_BASE64(2),
    ID_CARD_BASE64(3),
    LIVENESS(4),
    ID_CARD_BACK(5),
    LIVENESS_BASE64(6),
    ID_CARD_BACK_BASE64(7);

    private final int value;
    ImageType(int value){
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static ImageType fromValue(int value){
        for (ImageType s : values()) {
            if (s.value == value) {
                return s;
            }
        }
        return null;
    }

}
