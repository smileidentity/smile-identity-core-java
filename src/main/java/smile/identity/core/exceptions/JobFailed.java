package smile.identity.core.exceptions;

public class JobFailed extends Exception{
    private String errorCode;

    public JobFailed(String message, String errorCode){
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
