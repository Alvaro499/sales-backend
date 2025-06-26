package ucr.ac.cr.authentication.models;

import ucr.ac.cr.authentication.models.ErrorCode;

public class BaseException extends RuntimeException {

  private ErrorCode errorCode;
  private String message;
  private Object[] params;

  public BaseException(String message) {
    super(message);
    this.errorCode = ErrorCode.GENERAL_ERROR;
    this.message = message;
    this.params = new Object[0];
  }

  public BaseException(ErrorCode errorCode, String message, Object... params) {
    super(message);
    this.errorCode = errorCode;
    this.message = message;
    this.params = params;
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }

  public String getMessage() {
    return message;
  }

  public Object[] getParams() {
    return params;
  }

  public static BaseException exceptionBuilder() {
    return new BaseException(null, null);
  }

  public BaseException code(String code) {
    this.errorCode = ErrorCode.valueOf(code);
    return this;
  }

  public BaseException message(String message) {
    this.message = message;
    return this;
  }

  public BaseException params(Object... params) {
    this.params = params;
    return this;
  }

  public BaseException build() {
    if (this.errorCode == null || this.message == null) {
      throw new IllegalStateException("Error code and message must be set before building the exception.");
    }
    return new BaseException(this.errorCode, this.message, this.params);
  }
}
