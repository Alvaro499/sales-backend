package ucr.ac.cr.authentication.client.basic;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ucr.ac.cr.authentication.models.PymeResponse;

import java.util.UUID;

@FeignClient(name = "ventas-back")
public interface PymeClient {

    @GetMapping("/api/pymes/internal/by-email")
    PymeResponse getByEmail(@RequestParam String email);

    @PutMapping("/api/pymes/internal/activate")
    void activatePyme(@RequestParam UUID pymeId);
}
