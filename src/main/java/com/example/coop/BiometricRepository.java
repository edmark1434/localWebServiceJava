package com.example.coop;

import com.example.coop.Biometric;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BiometricRepository extends JpaRepository<Biometric, Long> {
    Optional<Biometric> findByUserId(Long userId);
}