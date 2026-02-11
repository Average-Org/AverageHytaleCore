package exceptions;

public class HytaleIncompatibilityException extends RuntimeException {
    private final String code;

    public HytaleIncompatibilityException(String code, String message, Throwable cause){
        super(String.format("[%s] %s", code, message), cause);
        this.code = code;
    }

    public String getCode(){
        return code;
    }
}
