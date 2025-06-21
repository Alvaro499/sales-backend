package ucr.ac.cr.BackendVentas.events;

import java.util.UUID;

public class SendUserPymeIdEvent {
    private final UserRegistrationDTO userRegistrationDTO;

    public SendUserPymeIdEvent(UUID pymeId, UUID userId) {
        this.userRegistrationDTO = new UserRegistrationDTO(pymeId, userId);
    }

    public UserRegistrationDTO getUserRegistrationDTO() {
        return userRegistrationDTO;
    }


}
