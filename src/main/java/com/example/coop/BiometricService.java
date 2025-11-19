package com.example.coop;

import com.zkteco.biometric.FingerprintSensorEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BiometricService {

    @Autowired
    private BiometricRepository biometricRepository;

    private long deviceHandle = 0;
    private long dbHandle = 0;

    // Initialize scanner and DB
    private boolean initScanner() {
        int init = FingerprintSensorEx.Init();
        if (init != 0) return false;

        int count = FingerprintSensorEx.GetDeviceCount();
        if (count <= 0) return false;

        deviceHandle = FingerprintSensorEx.OpenDevice(0);
        dbHandle = FingerprintSensorEx.DBInit();

        return deviceHandle != 0 && dbHandle != 0;
    }

    private void closeScanner() {
        if (deviceHandle != 0) FingerprintSensorEx.CloseDevice(deviceHandle);
        FingerprintSensorEx.Terminate();
    }

    // 1️⃣ Register fingerprint
    public Biometric registerFingerprint(Long userId) {
        if (!initScanner()) return null;

        byte[] imgBuffer = new byte[2048];
        byte[] template = new byte[2048];
        int[] size = new int[]{2048};

        int result = FingerprintSensorEx.AcquireFingerprint(deviceHandle, imgBuffer, template, size);
        closeScanner();

        if (result != 0) return null;

        byte[] regTemplate = new byte[size[0]];
        System.arraycopy(template, 0, regTemplate, 0, size[0]);

        Biometric biometric = new Biometric(userId, regTemplate);
        return biometricRepository.save(biometric);
    }

    // 2️⃣ Verify fingerprint for a specific user
    public boolean verifyFingerprint(Long userId) {
        Optional<Biometric> optional = biometricRepository.findByUserId(userId);
        if (optional.isEmpty()) return false;

        Biometric biometric = optional.get();
        byte[] storedTemplate = biometric.getTemplate();

        if (!initScanner()) return false;

        byte[] imgBuffer = new byte[2048];
        byte[] template = new byte[2048];
        int[] size = new int[]{2048};

        int result = FingerprintSensorEx.AcquireFingerprint(deviceHandle, imgBuffer, template, size);
        closeScanner();

        if (result != 0) return false;

        byte[] capturedTemplate = new byte[size[0]];
        System.arraycopy(template, 0, capturedTemplate, 0, size[0]);

        // Compare template
        int matchResult = FingerprintSensorEx.DBMatch(dbHandle, storedTemplate, capturedTemplate);
        return matchResult != 0;
    }

    // 3️⃣ Identify fingerprint among all users
    public Long identifyFingerprint() {
        List<Biometric> allBiometrics = biometricRepository.findAll();
        if (allBiometrics.isEmpty()) return null;

        if (!initScanner()) return null;

        byte[] imgBuffer = new byte[2048];
        byte[] template = new byte[2048];
        int[] size = new int[]{2048};

        int result = FingerprintSensorEx.AcquireFingerprint(deviceHandle, imgBuffer, template, size);
        if (result != 0) {
            closeScanner();
            return null;
        }

        byte[] capturedTemplate = new byte[size[0]];
        System.arraycopy(template, 0, capturedTemplate, 0, size[0]);

        for (Biometric b : allBiometrics) {
            int match = FingerprintSensorEx.DBMatch(dbHandle, b.getTemplate(), capturedTemplate);
            if (match != 0) {
                closeScanner();
                return b.getUserId();
            }
        }

        closeScanner();
        return null; // No match
    }
    // Health check for SDK
    public boolean isSdkReady() {
        int init = FingerprintSensorEx.Init();
        if (init != 0) return false;

        int deviceCount = FingerprintSensorEx.GetDeviceCount();
        if (deviceCount <= 0) {
            FingerprintSensorEx.Terminate();
            return false;
        }

        deviceHandle = FingerprintSensorEx.OpenDevice(0);
        if (deviceHandle == 0) {
            FingerprintSensorEx.Terminate();
            return false;
        }

        dbHandle = FingerprintSensorEx.DBInit();
        if (dbHandle == 0) {
            FingerprintSensorEx.CloseDevice(deviceHandle);
            FingerprintSensorEx.Terminate();
            return false;
        }

        // Close handles after checking
        FingerprintSensorEx.DBFree(dbHandle);
        FingerprintSensorEx.CloseDevice(deviceHandle);
        FingerprintSensorEx.Terminate();

        return true;
    }
    // Test saving a dummy fingerprint to DB
    public Biometric testStoreFingerprint(Long userId) {
        try {
            // Create a dummy template (fake fingerprint)
            byte[] dummyTemplate = new byte[512]; // arbitrary size
            for (int i = 0; i < dummyTemplate.length; i++) {
                dummyTemplate[i] = (byte) (i % 256);
            }

            Biometric biometric = new Biometric(userId, dummyTemplate);

            // Save to database
            Biometric saved = biometricRepository.save(biometric);

            return saved; // return saved object
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public List<Biometric> getAllBiometrics() {
        return biometricRepository.findAll();
    }
}