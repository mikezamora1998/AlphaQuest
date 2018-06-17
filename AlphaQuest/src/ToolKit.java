import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.imageio.ImageIO;

public class ToolKit {
	
	public String assetsPath = "/assets";
	
	public ToolKit() {
		
	}

	public BufferedImage loadImage(String path) {
		try {
			BufferedImage loadedImage = ImageIO.read(filePathURL(path));
			BufferedImage formattedImage = new BufferedImage(loadedImage.getWidth(), loadedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
			formattedImage.getGraphics().drawImage(loadedImage ,0 ,0 , null);
			
			return formattedImage;
		}catch(IOException exception){
			exception.printStackTrace();
			return null;
		}
	}
	
	public URL filePathURL(String path) {
		String dir = getClass().getResource("/" + getClass().getName().replaceAll("\\.", "/") + ".class").toString();
		
		if (!dir.contains(".exe"))
			return Game.class.getResource(path);
		
		dir = dir.substring(4).replaceFirst("/[^/]+\\.exe!.*$", "/");
		dir = dir + assetsPath + path;
        try {
            return new URL(dir);
        } catch (MalformedURLException e) {
        	e.printStackTrace();
        }
        
        return null;
	}
	
	public String filePathString(String path) {
		String url = getClass().getResource("/" + getClass().getName().replaceAll("\\.", "/") + ".class").toString();
		
		if(!url.contains(".exe"))
			return assetsPath.replace("/", "") + path;
		
        url = url.substring(4).replaceFirst("/[^/]+\\.exe!.*$", "/");
        url = url + assetsPath + path;
        try {
            File dir = new File(new URL(url).toURI());
            url = dir.getAbsolutePath();
        } catch (MalformedURLException e) {
            url = null;
            e.printStackTrace();
        } catch (URISyntaxException e) {
            url = null;
            e.printStackTrace();
        }
        return url;
	}
}
