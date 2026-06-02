import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
// used to construct a secret key from a raw byte arr(AES algo)

public class AESUtil {
    public static byte[] encrypt(byte[] data, String key) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(fixKey(key), "AES"); // fixkey() private helper method 
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(data); // performs encryption in input data byte arr
    }

    public static byte[] decrypt(byte[] data, String key) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(fixKey(key), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }

    private static byte[] fixKey(String key) {
        byte[] bytes = new byte[16]; // necessary because aes keys must be specific len [AES-128]
        byte[] keyBytes = key.getBytes(); // CONV the input string key into byte arr
        for (int i = 0; i < 16; i++) {
            bytes[i] = i < keyBytes.length ? keyBytes[i] : 0;
        }
        return bytes;
    }
}
