package com.example.phonebook.repository;

import com.example.phonebook.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Long> {
    @Query("SELECT DISTINCT c FROM Contact c LEFT JOIN c.phones p " +
           "WHERE c.name LIKE %:keyword% OR p.phone LIKE %:keyword% " +
           "ORDER BY c.isFavorite DESC, c.createTime DESC")
    List<Contact> searchContacts(String keyword);
    
    @Query("SELECT c FROM Contact c ORDER BY c.isFavorite DESC, c.createTime DESC")
    List<Contact> findAllOrderByFavoriteAndCreateTime();
    
    @Modifying
    @Transactional
    @Query("UPDATE Contact c SET c.isFavorite = :isFavorite WHERE c.id = :id")
    void updateFavoriteStatus(Long id, Boolean isFavorite);
    
    @Modifying
    @Transactional
    void deleteByIdIn(List<Long> ids);
} 