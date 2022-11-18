package smile.identity.core.exceptions;

public class IncorrectJobType extends Exception {

    public IncorrectJobType(int jobType, String service) {
        super(String.format("Please ensure that you are setting your job_type" +
                " to %d for %s", jobType, service));
    }
}
