package dynamicdudes.exception;

public class UsernameAlreadyRegisteredException extends RuntimeException {

    public UsernameAlreadyRegisteredException() {
        super("Username is already registered");
    }
}
