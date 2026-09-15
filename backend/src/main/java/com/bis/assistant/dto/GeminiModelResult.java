package com.bis.assistant.dto;

/**
 * Result of a Google Gemini API invocation attempt for a specific model.
 */
public class GeminiModelResult {

    private final boolean success;
    private final String text;
    private final String modelName;
    private final int httpStatus;
    private final String errorMessage;
    private final boolean retryable;
    private final boolean authError;

    public GeminiModelResult(boolean success, String text, String modelName, int httpStatus,
                             String errorMessage, boolean retryable, boolean authError) {
        this.success = success;
        this.text = text;
        this.modelName = modelName;
        this.httpStatus = httpStatus;
        this.errorMessage = errorMessage;
        this.retryable = retryable;
        this.authError = authError;
    }

    public static GeminiModelResult success(String text, String modelName) {
        return new GeminiModelResult(true, text, modelName, 200, null, false, false);
    }

    public static GeminiModelResult failure(String modelName, int httpStatus, String errorMessage,
                                            boolean retryable, boolean authError) {
        return new GeminiModelResult(false, null, modelName, httpStatus, errorMessage, retryable, authError);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getText() {
        return text;
    }

    public String getModelName() {
        return modelName;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isRetryable() {
        return retryable;
    }

    public boolean isAuthError() {
        return authError;
    }

    @Override
    public String toString() {
        return "GeminiModelResult{" +
                "success=" + success +
                ", modelName='" + modelName + '\'' +
                ", httpStatus=" + httpStatus +
                ", retryable=" + retryable +
                ", authError=" + authError +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
