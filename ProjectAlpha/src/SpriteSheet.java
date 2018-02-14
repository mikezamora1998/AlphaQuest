import java.awt.image.BufferedImage;

public class SpriteSheet {
	private int[] pixels;
	private BufferedImage image;
	public final int SIZEX;
	public final int SIZEY;
	
	public SpriteSheet(BufferedImage sheetImage) {
		image = sheetImage;
		SIZEX = sheetImage.getWidth();
		SIZEY = sheetImage.getHeight();
		
		pixels = new int[SIZEX*SIZEY];
		pixels = sheetImage.getRGB(0, 0, SIZEX, SIZEY, pixels, 0, SIZEX);
	}
	
	// 1:54
	
	public int[] getPixels() {
		return pixels;
	}
	
	public BufferedImage getImage() {
		return image;
	}
}
