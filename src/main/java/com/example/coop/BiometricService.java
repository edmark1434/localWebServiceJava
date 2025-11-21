package com.example.coop;

import com.zkteco.biometric.FingerprintSensor;
import com.zkteco.biometric.FingerprintSensorErrorCode;
import com.zkteco.biometric.FingerprintSensorEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class BiometricService {

    @Autowired
    private BiometricRepository biometricRepository;

    // 1️⃣ Scan fingerprint template (unchanged)
    public byte[] scanFingerprintTemplate() throws InterruptedException {
        FingerprintSensor sensor = new FingerprintSensor();
        if (sensor.openDevice(0) != FingerprintSensorErrorCode.ERROR_SUCCESS) {
            System.out.println("❌ Cannot open device");
            return null;
        }

        byte[] imgBuffer = new byte[sensor.getImageWidth() * sensor.getImageHeight()];
        byte[] template = new byte[2048];
        int[] tplSize = new int[]{2048};

        System.out.println("Place your finger...");

        while (true) {
            int imgResult = sensor.capture(imgBuffer, template, tplSize);

            if (imgResult == FingerprintSensorErrorCode.ERROR_SUCCESS && tplSize[0] > 0) {
                byte[] finalTpl = new byte[tplSize[0]];
                System.arraycopy(template, 0, finalTpl, 0, tplSize[0]);
                sensor.closeDevice();
                return finalTpl;
            } else if (imgResult == FingerprintSensorErrorCode.ZKFP_ERR_CAPTURE) {
                System.out.println("Waiting for proper finger placement...");
            }

            Thread.sleep(500);
        }
    }

    // 2️⃣ Register fingerprint
    public Biometric registerFingerprintDirect(Long userId) throws InterruptedException {
        byte[] regTemplate = scanFingerprintTemplate();
        if (regTemplate == null) return null;

        Biometric biometric = new Biometric(userId, regTemplate);
        return biometricRepository.save(biometric);
    }

    // 3️⃣ Verify fingerprint for a specific user
    public boolean verifyFingerprintDirect(Long userId) throws InterruptedException {
        Optional<Biometric> optional = biometricRepository.findByUserId(userId);
        if (optional.isEmpty()) return false;

        byte[] storedTemplate = optional.get().getTemplate();
        byte[] capturedTemplate = scanFingerprintTemplate();
        if (capturedTemplate == null) return false;

        long dbHandle = FingerprintSensorEx.DBInit();
        int matchResult = FingerprintSensorEx.DBMatch(dbHandle, storedTemplate, capturedTemplate);
        FingerprintSensorEx.DBFree(dbHandle);

        return matchResult != 0;
    }

    // 4️⃣ Another verify method
    public boolean verifyFingerprint(Long userId) throws InterruptedException {
        return verifyFingerprintDirect(userId); // reuse
    }

    // 5️⃣ Identify fingerprint among all users
    public Long identifyFingerprint() throws InterruptedException {
        List<Biometric> allBiometrics = biometricRepository.findAll();
        if (allBiometrics.isEmpty()) return null;

        byte[] capturedTemplate = scanFingerprintTemplate();
        if (capturedTemplate == null) return null;

        long dbHandle = FingerprintSensorEx.DBInit();
        for (Biometric b : allBiometrics) {
            int match = FingerprintSensorEx.DBMatch(dbHandle, b.getTemplate(), capturedTemplate);
            if (match != 0) {
                FingerprintSensorEx.DBFree(dbHandle);
                return b.getUserId();
            }
        }
        FingerprintSensorEx.DBFree(dbHandle);
        return null;
    }

    // 6️⃣ Test saving a dummy fingerprint
    public Biometric testStoreFingerprint(Long userId) {
        try {
            byte[] dummyTemplate = new byte[512];
            for (int i = 0; i < dummyTemplate.length; i++) dummyTemplate[i] = (byte) (i % 256);

            Biometric biometric = new Biometric(userId, dummyTemplate);
            return biometricRepository.save(biometric);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Biometric> getAllBiometrics() {
        return biometricRepository.findAll();
    }
}
