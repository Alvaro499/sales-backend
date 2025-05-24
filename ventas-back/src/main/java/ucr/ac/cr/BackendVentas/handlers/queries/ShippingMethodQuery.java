package ucr.ac.cr.BackendVentas.handlers.queries;

public interface ShippingMethodQuery {

    boolean existsByNameIgnoreCaseAndIsActiveTrue(String name);

}
