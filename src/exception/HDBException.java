package exception;

/**
 * Custom exception class for BTO Housing System
 */
public class HDBException extends Exception {
    
    public enum ErrorType {
        VALIDATION_ERROR,
        NOT_FOUND_ERROR,
        AUTHORIZATION_ERROR,
        BUSINESS_RULE_ERROR,
        SYSTEM_ERROR
    }
    
    private final ErrorType errorType;
    
    /**
     * Constructor for HDBException
     * 
     * @param message Error message
     * @param errorType Type of error
     */
    public HDBException(String message, ErrorType errorType) {
        super(message);
        this.errorType = errorType;
    }
    
    /**
     * Constructor for HDBException with cause
     * 
     * @param message Error message
     * @param cause The cause of the exception
     * @param errorType Type of error
     */
    public HDBException(String message, Throwable cause, ErrorType errorType) {
        super(message, cause);
        this.errorType = errorType;
    }
    
    /**
     * Gets the error type
     * 
     * @return The error type
     */
    public ErrorType getErrorType() {
        return errorType;
    }
    
    /**
     * Creates a validation error exception
     * 
     * @param message Error message
     * @return A new HDBException
     */
    public static HDBException validationError(String message) {
        return new HDBException(message, ErrorType.VALIDATION_ERROR);
    }
    
    /**
     * Creates a not found error exception
     * 
     * @param message Error message
     * @return A new HDBException
     */
    public static HDBException notFoundError(String message) {
        return new HDBException(message, ErrorType.NOT_FOUND_ERROR);
    }
    
    /**
     * Creates an authorization error exception
     * 
     * @param message Error message
     * @return A new HDBException
     */
    public static HDBException authorizationError(String message) {
        return new HDBException(message, ErrorType.AUTHORIZATION_ERROR);
    }
    
    /**
     * Creates a business rule error exception
     * 
     * @param message Error message
     * @return A new HDBException
     */
    public static HDBException businessRuleError(String message) {
        return new HDBException(message, ErrorType.BUSINESS_RULE_ERROR);
    }
    
    /**
     * Creates a system error exception
     * 
     * @param message Error message
     * @param cause The cause of the exception
     * @return A new HDBException
     */
    public static HDBException systemError(String message, Throwable cause) {
        return new HDBException(message, cause, ErrorType.SYSTEM_ERROR);
    }
} 