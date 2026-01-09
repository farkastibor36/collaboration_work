package exceptions;

public class FailedToDeleteException extends Exception {
    public FailedToDeleteException(String message) {
        super(message);
    }
}
