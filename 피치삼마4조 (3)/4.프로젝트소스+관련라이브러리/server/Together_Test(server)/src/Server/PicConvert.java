package Server;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

import javax.imageio.ImageIO;

public class PicConvert {
	public String BitmapToString(String path) {
		
		String imageString = null;
		try {
			
			File file = new File(path);
			FileInputStream fls = new FileInputStream(file);
			ByteArrayOutputStream bs = new ByteArrayOutputStream();

			int len = 0;
			byte[] buf = new byte[1024];
			while((len = fls.read(buf)) != -1) {
				bs.write(buf,0,len);
			}
			
			byte[] fileArray = bs.toByteArray();
			imageString = new String(Base64.getEncoder().encode(fileArray));
			
			fls.close();
			
			
			/*
			 * BufferedImage bi = ImageIO.read(file);
			 * 
			 * ByteArrayOutputStream bos = new ByteArrayOutputStream();
			 * ImageIO.write(bi,"jpg",bos); byte[] imgByte = bos.toByteArray();
			 * 
			 * imageString = Base64.getEncoder().encodeToString(imgByte);
			 * 
			 * bos.close();
			 * 
			 * System.out.println("데이터크기: " + imageString); byte[] encodedbyte =
			 * imageString.getBytes(); System.out.println(new String(encodedbyte));
			 */
			return imageString;
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		
	}
	
	public BufferedImage StringToBitmap(String encodedString) {
		BufferedImage image = null;
        byte[] imageByte;
        try {
            imageByte = Base64.getDecoder().decode(encodedString);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
            image = ImageIO.read(bis);
            ImageIO.write(image, "jpeg", new File("D:\\AndroidStudio-Study-Github\\Together\\Together_Test\\Picture"));
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
	}
	
	public void byteToBitmap(byte[] pic) {
		BufferedImage image =null;
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(pic);
			image = ImageIO.read(bis);
			
			ImageIO.write(image,"jpeg",new File("D:\\AndroidStudio-Study-Github\\Together\\Together_Test\\Picture\\test.jpg"));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
