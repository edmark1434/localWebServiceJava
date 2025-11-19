
base url = http://localhost:8080/api/biometric

/register/{userId} → calls registerFingerprint

/verify/{userId} → calls verifyFingerprint

/identify → calls identifyFingerprint

/status → SDK health check

/test-store/{userId} → saves a dummy fingerprint

/all → fetches all stored fingerprints

spring.application.name=coop
spring.datasource.url=jdbc:postgresql://localhost:5432/coop
spring.datasource.username=postgres
spring.datasource.password=F1R3eX17
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

paste ni sa katong imong application.properties naa located sa src/main/resources/application.properties
