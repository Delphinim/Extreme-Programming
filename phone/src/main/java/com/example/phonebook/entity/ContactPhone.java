package com.example.phonebook.entity;

import lombok.Data;
import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Data
@Entity
@Table(name = "contact_phone")
public class ContactPhone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id", nullable = false)
    @JsonBackReference
    private Contact contact;
    
    private String phone;
    private String type;
    private String description;
} 