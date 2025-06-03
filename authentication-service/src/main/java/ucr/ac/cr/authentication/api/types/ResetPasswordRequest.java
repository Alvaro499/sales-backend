package ucr.ac.cr.authentication.api.types;

import java.util.UUID;

public record ResetPasswordRequest(String token, String newPassword) {}
