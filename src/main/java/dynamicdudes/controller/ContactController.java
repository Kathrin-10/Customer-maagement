package dynamicdudes.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dynamicdudes.dto.ContactRequest;
import dynamicdudes.service.ContactService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/contacts")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping
    public ResponseEntity<?> submitContact(
            @Valid @RequestBody ContactRequest request) {
        System.out.println("CHECK 4 - ContactController reached");
        contactService.saveContact(request);

        return ResponseEntity.ok(
                java.util.Map.of(
                        "message",
                        "Thank you! Your message has been sent successfully."
                )
        );
    }
}
