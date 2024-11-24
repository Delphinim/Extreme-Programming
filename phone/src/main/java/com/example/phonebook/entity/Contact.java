package com.example.phonebook.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "contact")
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String email;
    private String address;
    private String wechat;
    private String qq;
    private String weibo;
    
    @OneToMany(mappedBy = "contact", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ContactPhone> phones = new ArrayList<>();
    
    @CreationTimestamp
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    @Column(name = "is_favorite")
    private Boolean isFavorite = false;
    
    public void addPhone(ContactPhone phone) {
        phones.add(phone);
        phone.setContact(this);
    }
    
    public void removePhone(ContactPhone phone) {
        phones.remove(phone);
        phone.setContact(null);
    }
} 
