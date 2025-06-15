package ucr.ac.cr.email_service.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public class TryCatchWrapper {

    private static final Logger logger = LoggerFactory.getLogger(TryCatchWrapper.class);

    public static <T> T runOrLog(CheckedSupplier<T> block, String contextMessage) {
        try {
            return block.get();
        } catch (Exception e) {
            logger.error("Error en contexto '{}': {} - stacktrace: ", contextMessage, e.getMessage(), e);
            return null;
        }
    }

    public static void runOrLog(Runnable block, String contextMessage) {
        try {
            block.run();
        } catch (Exception e) {
            logger.error("Error en contexto '{}': {} - stacktrace: ", contextMessage, e.getMessage(), e);
        }
    }
}
