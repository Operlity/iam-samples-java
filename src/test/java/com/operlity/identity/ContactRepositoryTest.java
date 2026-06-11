package com.operlity.identity;

import com.operlity.identity.model.Contact;
import com.operlity.identity.repository.ContactRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ContactRepositoryTest {

    @Autowired
    private ContactRepository contactRepository;

    @Test
    public void testContactCrud() {
        // 1. Create
        Contact contact = new Contact("user@test.com", "John Doe", "john@test.com", "123456789", "Acme", "Developer", "Nice guy");
        Contact saved = contactRepository.save(contact);
        
        assertNotNull(saved.getId());
        assertEquals("John Doe", saved.getName());
        assertNotNull(saved.getCreatedAt());

        // 2. Read
        List<Contact> contacts = contactRepository.findByUserIdOrderByNameAsc("user@test.com");
        assertFalse(contacts.isEmpty());
        assertEquals(1, contacts.size());
        assertEquals("John Doe", contacts.get(0).getName());

        // 3. Update
        saved.setName("John Updated");
        Contact updated = contactRepository.save(saved);
        assertEquals("John Updated", updated.getName());

        // 4. Delete
        contactRepository.delete(updated);
        Optional<Contact> deleted = contactRepository.findByIdAndUserId(updated.getId(), "user@test.com");
        assertTrue(deleted.isEmpty());
    }
}
