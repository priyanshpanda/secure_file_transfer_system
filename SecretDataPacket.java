import java.io.Serializable;

public class SecretDataPacket implements Serializable { // serializable part -> obj can be easily sent/ received
    private static final long serialVersionUID = 1L; // helps java runtime env verify s & r working with same ver  class
    private String fileName;
    private byte[] encryptedData;

    public SecretDataPacket(String fileName, byte[] encryptedData) {
        this.fileName = fileName;
        this.encryptedData = encryptedData;
    }

    public String getFileName() { //standard getter method
        return fileName;
    }

    public byte[] getEncryptedData() {
        return encryptedData;
    }
}
/*This class acts as a simple data structure or container to
 bundle the file name and the encrypted file contents together
so they can be reliably sent as a single object (a "packet") over a network. */