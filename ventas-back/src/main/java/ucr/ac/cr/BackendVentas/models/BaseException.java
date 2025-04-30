package ucr.ac.cr.BackendVentas.models;

import java.util.List;

public class BaseException extends RuntimeException {
    private final String code;
    private final String message;
    private final List<String> params;

    private BaseException(String code, String message, List<String> params) {
        super(message);
        this.code = code;
        this.message = message;
        this.params = params;
    }

    public static ExceptionBuilder exceptionBuilder() {
        return new ExceptionBuilder();
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getParams() {
        return params;
    }

    public static class ExceptionBuilder {
        private String code;
        private String message;
        private List<String> params;

        public ExceptionBuilder code(String code) {
            this.code = code;
            return this;
        }

        public ExceptionBuilder message(String message) {
            this.message = message;
            return this;
        }

        public ExceptionBuilder params(List<String> params) {
            this.params = params;
            return this;
        }

        public BaseException build() {
            return new BaseException(code, message, params);
        }
    }
}
