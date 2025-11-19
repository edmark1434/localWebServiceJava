package com.example.coop;

import com.example.coop.Biometric;
import com.example.coop.BiometricService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/biometric")
public class BiometricController {

    @Autowired
    private BiometricService biometricService;

    // -------------------------------
    // 1️⃣ Registration: store user_id + template
    // -------------------------------
    @PostMapping("/register/{userId}")
    public ResponseEntity<Biometric> register(@PathVariable Long userId) {
        Biometric biometric = biometricService.registerFingerprint(userId);
        if (biometric == null) {
            return ResponseEntity.status(500).body(null);
        }
        return ResponseEntity.ok(biometric);
    }

    // -------------------------------
    // 2️⃣ Verification: verify fingerprint for a specific user
    // -------------------------------
    @PostMapping("/verify/{userId}")
    public ResponseEntity<String> verify(@PathVariable Long userId) {
        boolean verified = biometricService.verifyFingerprint(userId);
        if (verified) {
            return ResponseEntity.ok("Fingerprint verified successfully");
        } else {
            return ResponseEntity.status(401).body("Fingerprint verification failed");
        }
    }

    // -------------------------------
    // 3️⃣ Identification: scan fingerprint and return userId
    // -------------------------------
    @PostMapping("/identify")
    public ResponseEntity<Long> identify() {
        Long userId = biometricService.identifyFingerprint();
        if (userId == null) {
            return ResponseEntity.status(404).build();
        }
        return ResponseEntity.ok(userId);
    }
    @GetMapping("/status")
    public ResponseEntity<String> status() {
        boolean ready = biometricService.isSdkReady();
        if (ready) {
            return ResponseEntity.ok("SDK is properly initialized and scanner is ready.");
        } else {
            return ResponseEntity.status(503).body("SDK initialization failed or scanner not detected.");
        }
    }
    @GetMapping("/test-store/{userId}")
    public ResponseEntity<Biometric> testStore(@PathVariable Long userId) {
        Biometric result = biometricService.testStoreFingerprint(userId);
        if (result == null) {
            return ResponseEntity.status(500).body(null);
        }
        return ResponseEntity.ok(result);
    }
    @GetMapping("/all")
    public ResponseEntity<List<Biometric>> getAll() {
        List<Biometric> list = biometricService.getAllBiometrics();
        if (list.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(list);
    }
}
