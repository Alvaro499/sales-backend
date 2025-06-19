package ucr.ac.cr.email_service.utils;


/**
 * Functional interface similar a Supplier<T> pero que permite lanzar excepciones checked.
 */
@FunctionalInterface
public interface CheckedSupplier<T> {
    T get() throws Exception;
}
