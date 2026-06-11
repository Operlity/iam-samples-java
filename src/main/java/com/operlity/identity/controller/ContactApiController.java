package com.operlity.identity.controller;

import com.operlity.identity.model.Contact;
import com.operlity.identity.repository.ContactRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/contacts")
public class ContactApiController {

    private final ContactRepository contactRepository;

    public ContactApiController(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    private String getUserId(OidcUser principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        return principal.getEmail();
    }

    @GetMapping
    public List<Contact> getContacts(@AuthenticationPrincipal OidcUser principal) {
        return contactRepository.findByUserIdOrderByNameAsc(getUserId(principal));
    }

    @PostMapping
    public Contact createContact(@RequestBody Contact contact, @AuthenticationPrincipal OidcUser principal) {
        if (contact.getName() == null || contact.getName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name is required");
        }
        contact.setId(null);
        contact.setUserId(getUserId(principal));
        return contactRepository.save(contact);
    }

    @PutMapping("/{id}")
    public Contact updateContact(@PathVariable Long id, @RequestBody Contact contactDetails, @AuthenticationPrincipal OidcUser principal) {
        String userId = getUserId(principal);
        Contact contact = contactRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

        if (contactDetails.getName() == null || contactDetails.getName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name is required");
        }
        
        contact.setName(contactDetails.getName());
        contact.setEmail(contactDetails.getEmail());
        contact.setPhone(contactDetails.getPhone());
        contact.setCompany(contactDetails.getCompany());
        contact.setJobTitle(contactDetails.getJobTitle());
        contact.setNotes(contactDetails.getNotes());

        return contactRepository.save(contact);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteContact(@PathVariable Long id, @AuthenticationPrincipal OidcUser principal) {
        String userId = getUserId(principal);
        Contact contact = contactRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

        contactRepository.delete(contact);
        return ResponseEntity.ok().build();
    }
}
