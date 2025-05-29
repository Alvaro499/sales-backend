package ucr.ac.cr.authentication.api.types;

import java.util.UUID;

public record ResetPasswordRequest(UUID token, String newPassword) {}
