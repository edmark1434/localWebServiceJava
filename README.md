
base url = http://localhost:8080/api/biometric

/register/{userId} → calls registerFingerprint

/verify/{userId} → calls verifyFingerprint

/identify → calls identifyFingerprint

/status → SDK health check

/test-store/{userId} → saves a dummy fingerprint

/all → fetches all stored fingerprints