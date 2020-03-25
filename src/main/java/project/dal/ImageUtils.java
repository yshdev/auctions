/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.dal;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
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
    
    public static BufferedImage loadImage(String fileName) throws IOException, URISyntaxException {

        ClassLoader classLoader = new ImageUtils().getClass().getClassLoader();

        InputStream input = classLoader.getResourceAsStream(fileName);
        return ImageIO.read(input);
        //return ImageIO.read(new File(fileNamej));
        //return ImageIO.read(new File(resource.getFile()));
    }
            
}
