package smile.identity.core.enums;

public enum JobType {
    ONE(1),
    TWO(2),
    FOUR(4),
    FIVE(5),
    SIX(6);

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

}
