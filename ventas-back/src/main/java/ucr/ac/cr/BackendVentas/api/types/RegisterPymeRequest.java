package ucr.ac.cr.BackendVentas.api.types;

public record RegisterPymeRequest(
    String pymeName,
    String email,
    String phone,
    String address,
    String password
) {}