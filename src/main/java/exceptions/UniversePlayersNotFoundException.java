package exceptions;

public class UniversePlayersNotFoundException extends HytaleIncompatibilityException {
    public static final String CODE = "6663";

    public UniversePlayersNotFoundException(Throwable cause) {
        super(CODE,
                "Could not find players field in Universe class." +
                        " This is likely due to a change in the Hytale server code." +
                        " Please report this to the developer.",
                cause);
    }
}
