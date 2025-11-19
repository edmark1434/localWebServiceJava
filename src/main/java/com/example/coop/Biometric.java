package com.example.coop;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Base64;

@Entity
@Table(name = "biometric_data")
public class Biometric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "template", nullable = false)
    private byte[] template;  // <-- map BYTEA to byte[]

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Biometric() {
    }

    public Biometric(Long userId, byte[] fingerprint) {
        this.userId = userId;
        this.template = fingerprint;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public byte[] getTemplate() {
        return Base64.getDecoder().decode(template);
    }

    public void setTemplate(byte[] template) {
        this.template = template;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
