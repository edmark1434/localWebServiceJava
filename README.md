
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
pachange lang sa pass ty

sample calls
//register
const userId = 79; // example user ID

fetch(`http://localhost:8080/api/biometric/register/${userId}`, {
method: 'POST',
})
.then(res => res.json())
.then(data => console.log('Registered fingerprint:', data))
.catch(err => console.error(err));

//verify
const userId = 79;

fetch(`http://localhost:8080/api/biometric/verify/${userId}`, {
method: 'POST',
})
.then(res => {
if (res.status === 200) return res.text();
else throw new Error('Verification failed');
})
.then(msg => console.log(msg))
.catch(err => console.error(err));

//identify
fetch('http://localhost:8080/api/biometric/identify', {
method: 'POST',
})
.then(res => {
if (res.status === 200) return res.json();
else throw new Error('No match found');
})
.then(userId => console.log('Matched user ID:', userId))
.catch(err => console.error(err));

  
