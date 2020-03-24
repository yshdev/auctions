/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.dal;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author Shalom
 */
public class ImageUtils {
    
    public static byte[] convertImageToBytes(BufferedImage bImage, String imageType) throws IOException {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      ImageIO.write(bImage, imageType, bos );
      byte[] data = bos.toByteArray();
      return data;
    }
    
    public static BufferedImage convertBytesToImage(byte[] buffer) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(buffer);
        BufferedImage image = ImageIO.read(bis);
        return image;
    }
            
}
