package com.example.coop;

import com.example.coop.Biometric;
import com.example.coop.BiometricService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
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
    public ResponseEntity<Biometric> register(@PathVariable Long userId) throws InterruptedException {
        Biometric biometric = biometricService.registerFingerprintDirect(userId);
        if (biometric == null) {
            return ResponseEntity.status(500).body(null);
        }
        return ResponseEntity.ok(biometric);
    }
    @GetMapping("/scan-template")
    public ResponseEntity<String> scanTemplate() throws InterruptedException {
        byte[] template = biometricService.scanFingerprintTemplate();
        if (template == null) {
            return ResponseEntity.status(500).body("Fingerprint scan failed or scanner not detected");
        }

//        if (biometricService.identifyFingerprint() != null) {
//            return ResponseEntity.status(409).body("Fingerprint already exists in the database");
//        }

        // Encode template as Base64 for PHP/Laravel compatibility
        String base64Template = Base64.getEncoder().encodeToString(template);
        return ResponseEntity.ok(base64Template);
    }
    // -------------------------------
    // 2️⃣ Verification: verify fingerprint for a specific user
    // -------------------------------
    @PostMapping("/verify/{userId}")
    public ResponseEntity<String> verifyDirect(@PathVariable Long userId) throws InterruptedException {
        boolean verified = biometricService.verifyFingerprintDirect(userId);
        if (verified) {
            return ResponseEntity.ok("Fingerprint verified successfully");
        } else {
            return ResponseEntity.status(401).body("Fingerprint verification failed");
        }
    }
    @PostMapping("/verifying/{userId}")
    public ResponseEntity<String> verify(@PathVariable Long userId) throws InterruptedException {
        boolean verified = biometricService.verifyFingerprint(userId);
        if (verified) {
            return ResponseEntity.ok("Fingerprint verified successfully.");
        } else {
            return ResponseEntity.status(401).body("Incorrect fingerprint. Try again.");
        }
    }
    // -------------------------------
    // 3️⃣ Identification: scan fingerprint and return userId
    // -------------------------------
    @PostMapping("/identify")
    public ResponseEntity<Long> identify() throws InterruptedException {
        Long userId = biometricService.identifyFingerprint();
        if (userId == null) {
            return ResponseEntity.status(404).build();
        }
        return ResponseEntity.ok(userId);
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
