# What is Field-Level Encryption

[Resource Link](https://medium.com/@gaddamnaveen192/encrypt-decrypt-database-records-at-field-level-in-spring-boot-096e21049559)

Normally, when you secure sensitive data in a database, you might encrypt:

* Entire DB (e.g., Transparent Data Encryption — TDE)
* Table level
* Column/Field level

> Field Level Encryption (FLE) means only specific fields/columns (like credit card number, SSN,
password, etc.) are encrypted before saving to DB and decrypted after fetching.

This helps balance:

* Performance (not everything is encrypted)
* Security (only sensitive fields protected)

**Where to Use**

* PII (Personally Identifiable Information) → Name, Email, SSN
* Financial data → Credit Card numbers, Bank accounts
* Healthcare data → Medical records, Patient IDs

**Approaches in Spring Boot**

1. Manual Encryption/Decryption in Service Layer → Use AES/RSA with JCE
2. Attribute Converter (JPA @Converter) → Automatic encryption/decryption for entity fields
3. Hibernate Interceptors/Listeners → More advanced, global approach

Example Implementation

1. Encryption Utility

```java
   import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AESUtil {

  private static final String ALGORITHM = "AES";
  private static final String KEY = "MySecretKey12345"; // 16 chars = 128-bit

  public static String encrypt(String data) {
    try {
      SecretKeySpec secretKey = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
      Cipher cipher = Cipher.getInstance(ALGORITHM);
      cipher.init(Cipher.ENCRYPT_MODE, secretKey);
      return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));
    } catch (Exception e) {
      throw new RuntimeException("Error while encrypting: ", e);
    }
  }

  public static String decrypt(String encryptedData) {
    try {
      SecretKeySpec secretKey = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
      Cipher cipher = Cipher.getInstance(ALGORITHM);
      cipher.init(Cipher.DECRYPT_MODE, secretKey);
      return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedData)));
    } catch (Exception e) {
      throw new RuntimeException("Error while decrypting: ", e);
    }
  }
}
```


2. JPA Attribute Converter for Field-Level Encryption

```java
   import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class EncryptionConverter implements AttributeConverter<String, String> {

  @Override
  public String convertToDatabaseColumn(String attribute) {
    if (attribute == null) {
      return null;
    }
    return AESUtil.encrypt(attribute);
  }

  @Override
  public String convertToEntityAttribute(String dbData) {
    if (dbData == null) {
      return null;
    }
    return AESUtil.decrypt(dbData);
  }

}
```


3. Entity with Encrypted Field

```java
 import jakarta.persistence.*;

@Entity
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;  // not encrypted

    @Convert(converter = EncryptionConverter.class)
    private String ssn;   // sensitive field, will be encrypted in DB

    @Convert(converter = EncryptionConverter.class)
    private String creditCard;  // sensitive

    // getters & setters

}
```

### Pros & Cons

Pros:

1. Only sensitive fields encrypted (performance efficient)
2. Automatic via @Converter (clean code)
3. Works with any JPA implementation

Cons:

* Searching/filtering on encrypted fields is hard (can’t query WHERE ssn = '1234' directly)
* Must manage encryption key securely (prefer Vault/KMS)
* Increases DB storage slightly (due to Base64 encoding)

Best Practices:

* Use environment-specific keys (not hardcoded like above). Example: AWS KMS, HashiCorp Vault.
* Rotate encryption keys regularly.
* Don’t encrypt fields you need to query/filter often.
* Use AES-256 instead of AES-128 for stronger security.