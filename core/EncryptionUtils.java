/**
 * <p> Title: Encryption Utilities </p>
 * 
 * <p> Description: Provides utility methods for encryption-related operations </p>
 * 
 * <p> Copyright: Copyright (c) 2024 </p>
 * 
 * @author ?
 * 
 * @version 1.0    2024-10-19    Initial implementation
 */
package core;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * This class provides utility methods for encryption-related operations,
 * including character and byte array conversions and initialization vector generation.
 */
public class EncryptionUtils {
    private static final int IV_SIZE = 16;
    
    /**
     * Converts a byte array to a character array.
     * 
     * @param bytes The byte array to convert
     * @return The resulting character array
     */
    public static char[] toCharArray(byte[] bytes) {		
        CharBuffer charBuffer = Charset.defaultCharset().decode(ByteBuffer.wrap(bytes));
        return Arrays.copyOf(charBuffer.array(), charBuffer.limit());
    }
    
    /**
     * Converts a character array to a byte array.
     * 
     * @param chars The character array to convert
     * @return The resulting byte array
     */
    static byte[] toByteArray(char[] chars) {		
        ByteBuffer byteBuffer = Charset.defaultCharset().encode(CharBuffer.wrap(chars));
        return Arrays.copyOf(byteBuffer.array(), byteBuffer.limit());
    }
        
    /**
     * Generates an initialization vector from the given text.
     * The method repeats the text if necessary to fill the IV_SIZE.
     * 
     * @param text The text to use for generating the initialization vector
     * @return The generated initialization vector as a byte array
     */
    public static byte[] getInitializationVector(char[] text) {
        char[] iv = new char[IV_SIZE];
        
        int textPointer = 0;
        int ivPointer = 0;
        while(ivPointer < IV_SIZE) {
            iv[ivPointer++] = text[textPointer++ % text.length];
        }
        
        return toByteArray(iv);
    }
    
    /**
     * Prints the contents of a character array to the console.
     * This method is primarily used for debugging purposes.
     * 
     * @param chars The character array to print
     */
    public static void printCharArray(char[] chars) {
        for(char c : chars) {
            System.out.print(c);
        }
        System.out.println(); // Add a new line after printing all characters
    }
}
