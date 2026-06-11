package com.operlity.identity.repository;

import com.operlity.identity.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    List<Contact> findByUserIdOrderByNameAsc(String userId);
    Optional<Contact> findByIdAndUserId(Long id, String userId);
}
