package ucr.ac.cr.BackendVentas.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ucr.ac.cr.BackendVentas.models.BaseException;
import ucr.ac.cr.BackendVentas.models.ErrorCode;

import java.util.List;

public class ValidationUtils {

    private static final Logger logger = LoggerFactory.getLogger(ValidationUtils.class);

    public static BaseException validationError(String msg, String code, String... params) {
        return BaseException.exceptionBuilder()
                .code(code)
                .message(msg)
                .params(List.of(params))
                .build();
    }

    public static void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            logger.warn("Validación fallida: email nulo o vacío");
            throw validationError("Debe ingresar un email", ErrorCode.REQUIRED_FIELDS, "email");
        }
        if (!email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            logger.warn("Validación fallida: formato inválido para email '{}'", email);
            throw validationError("El formato del email es inválido", ErrorCode.INVALID_FORMAT, "email");
        }
    }

    public static void validateName(String fieldName, String name) {
        if (name == null || name.isBlank()) {
            logger.warn("Validación fallida: '{}' está vacío o nulo", fieldName);
            throw validationError("Debes ingresar " + fieldName, ErrorCode.REQUIRED_FIELDS, fieldName);
        }

        if (name.length() > 100) {
            logger.warn("Validación fallida: '{}' supera los 100 caracteres (tiene {})", fieldName, name.length());
            throw validationError("El campo " + fieldName + " no puede tener más de 100 caracteres", ErrorCode.INVALID_FORMAT, fieldName);
        }
    }

    public static void validatePhone(String phone) {
        if (phone == null || phone.isBlank()) {
            logger.warn("Validación fallida: teléfono vacío o nulo");
            throw validationError("Debes ingresar un teléfono", ErrorCode.REQUIRED_FIELDS, "phone");
        }

        if (!phone.matches("[\\d-]{7,15}")) {
            logger.warn("Validación fallida: formato inválido para teléfono '{}'", phone);
            throw validationError("El formato del teléfono es inválido", ErrorCode.INVALID_FORMAT, "phone");
        }
    }

    public static void validateShippingAddress(String address) {
        if (address == null || address.isBlank()) {
            logger.warn("Validación fallida: dirección de envío vacía o nula");
            throw validationError("Debes ingresar una dirección de envío", ErrorCode.REQUIRED_FIELDS, "shippingAddress");
        }

        if (address.length() > 200) {
            logger.warn("Validación fallida: dirección de envío supera los 200 caracteres (tiene {})", address.length());
            throw validationError("La dirección de envío no puede tener más de 200 caracteres", ErrorCode.INVALID_FORMAT, "shippingAddress");
        }
    }

}
