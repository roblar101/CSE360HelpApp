/**
 * <p> Title: Encryption Helper </p>
 * 
 * <p> Description: Provides encryption and decryption functionality for the help system </p>
 * 
 * <p> Copyright: Copyright (c) 2024 </p>
 * 
 * @author Robby Larsen
 * 
 * @version 1.0    2024-10-15    Initial implementation
 */
package core;

import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * This class provides encryption and decryption functionality using AES algorithm.
 * It utilizes the Bouncy Castle provider for cryptographic operations.
 */
public class EncryptionHelper {

    private static final String BOUNCY_CASTLE_PROVIDER_IDENTIFIER = "BC";	
    private Cipher cipher;
    
    // AES key (24 bytes for AES-192)
    private static final byte[] keyBytes = new byte[] {
            0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
            0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f,
            0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17 };
    private SecretKey key = new SecretKeySpec(keyBytes, "AES");

    /**
     * Constructs a new EncryptionHelper and initializes the cipher.
     * 
     * @throws Exception If there's an error initializing the cipher
     */
    public EncryptionHelper() throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", BOUNCY_CASTLE_PROVIDER_IDENTIFIER);		
    }
    
    /**
     * Encrypts the given plaintext using AES algorithm.
     * 
     * @param plainText The text to be encrypted
     * @param initializationVector The initialization vector for CBC mode
     * @return The encrypted byte array
     * @throws Exception If there's an error during encryption
     */
    public byte[] encrypt(byte[] plainText, byte[] initializationVector) throws Exception {
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(initializationVector));
        return cipher.doFinal(plainText);
    }
    
    /**
     * Decrypts the given ciphertext using AES algorithm.
     * 
     * @param cipherText The text to be decrypted
     * @param initializationVector The initialization vector used for encryption
     * @return The decrypted byte array
     * @throws Exception If there's an error during decryption
     */
    public byte[] decrypt(byte[] cipherText, byte[] initializationVector) throws Exception {
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(initializationVector));
        return cipher.doFinal(cipherText);
    }
}
