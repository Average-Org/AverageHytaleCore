package exceptions;

public class UniversePlayersUnexpectedTypeException extends HytaleIncompatibilityException {
    public static final String CODE = "6664";

    public UniversePlayersUnexpectedTypeException(Throwable cause) {
        super(CODE,
                "Players was found, but it was not the expected type." +
                        " This is likely due to a change in the Hytale server code." +
                        " Please report this to the developer.",
                cause);
    }
}
