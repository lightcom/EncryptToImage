package encrypttoimage;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;

/**
 *
 * @author lightcom
 */
public class EncryptToImage {

    static String IV = "AAAAAAAAAAAAAAAA";
    static String plaintext = "Other message for instance \0\0\0";

    static String encKey = "2223334449abcdef";

    public static void main(String[] args) {
        try {
            System.out.println("Plain Text:   " + plaintext);

            byte[] cipher = encrypt(plaintext, encKey);
            
            BufferedImage img = getImageFromArray(cipher);
            ImageIO.write(img, "jpg", new File("./cipher.jpg"));
            
            System.out.print("Image 'cipher.jpg' generated.");
            
            byte[] decipher = getArrayFromImage(img);
            
            String decrypted = decrypt(decipher, encKey);
            
            System.out.println("Image 'cipher.jpg' decrypted.");
            
            System.out.println("Decrypted message: " + decrypted);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] encrypt(String plainText, String encKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
        SecretKeySpec key = new SecretKeySpec(encKey.getBytes("UTF-8"), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(IV.getBytes("UTF-8")));
        return cipher.doFinal(plainText.getBytes("UTF-8"));
    }

    public static String decrypt(byte[] cipherText, String encKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
        SecretKeySpec key = new SecretKeySpec(encKey.getBytes("UTF-8"), "AES");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(IV.getBytes("UTF-8")));
        return new String(cipher.doFinal(cipherText), "UTF-8");
    }
    
    public static BufferedImage getImageFromArray(byte[] cipher) {        
        int divider = 1;
        int width = cipher.length;
        int height  = 1;
        while(height < width)
        {
            divider = divider*2;
            width = cipher.length/divider;
            height = divider;
        }
        
        int[] pixels = new int[cipher.length];
        
        for (int i = 0; i < cipher.length; i++ ){
            pixels[i] = (int) cipher[i]  + 129;
        }
        
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster raster = (WritableRaster) image.getData();
        raster.setPixels(0,0,width,height,pixels);
        image.setData(raster);
        return image;
    }
    
    public static byte[] getArrayFromImage(BufferedImage image) {
        byte[] bytes = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        
        for (int i = 0; i < bytes.length; i++ ){
            bytes[i] = (byte) ((int)bytes[i] - 129);
        }
        
        return bytes;
    }
}
