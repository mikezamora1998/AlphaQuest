
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;


public class RenderHandler 
{
	private BufferedImage view;
	private Rectangle camera;
	private int[] pixels;

	public RenderHandler(int width, int height) 
	{
		//Create a BufferedImage that will represent our view.
		view = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		camera = new Rectangle(0, 0, width, height);
		
		//Camera is an object of rectangle class
		camera.x = 0;
		camera.y = 0;

		//Create an array for pixels
		pixels = ((DataBufferInt) view.getRaster().getDataBuffer()).getData();
		
	}

	//renders our pixel array t the screen.
	public void render(Graphics graphics)
	{
		
		// java - get screen size using the Toolkit class
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		// the screen height
		screenSize.getHeight();

		// the screen width
		screenSize.getWidth();

		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;
		
		//Prints screen resolution
		//System.out.println("screen Height: " + screenHeight + "\nScreen Width: " + screenWidth);
		
		//scaling to current screen resolution
		float scaleWidth = 1080 / screenHeight;	//screenHeight;
		float scaleHeight = 1920 / screenWidth;	//screenWidth;

		//checks if the resolution is equal to 16/9 aspect ratio
		if(scaleWidth < scaleHeight){
			scaleHeight = scaleWidth;
		}else{
			scaleWidth = scaleHeight;
		}
		
		Graphics2D g2 = (Graphics2D) graphics;
		
		//scales graphics
		g2.scale(scaleWidth, scaleHeight) ;
		
		//Renders to screen
		graphics.drawImage(view, 0, 0, view.getWidth(), view.getHeight(), null);

		//graphics.drawString("GRASSSSSS TILLLLLLE!!!!!!!!!!!!!!", 960, 540);
	}
	
	
	//Render an image to an array
	public void renderImage(BufferedImage image, int xPosition, int yPosition, int xZoom, int yZoom) {
		
		int[] imagePixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
		renderArray(imagePixels, image.getWidth(), image.getHeight(), xPosition, yPosition, xZoom, yZoom);
	}
	
	public void renderRectangle(Rectangle rectangle, int xZoom, int yZoom) {
		int[] rectanglePixels = rectangle.getPixels();
		if(rectanglePixels != null) {
			renderArray(rectanglePixels, rectangle.w, rectangle.h, rectangle.x, rectangle.y, xZoom, yZoom);
		}
	}
	
	public void renderArray(int[] renderPixels, int renderWidth, int renderHeigth, int xPosition, int yPosition, int xZoom, int yZoom) {
		for(int y = 0; y < renderHeigth; y++) {
			for(int x = 0; x < renderWidth; x++) {
				for(int yZoomPosistion = 0; yZoomPosistion < xZoom; yZoomPosistion++) {
					for(int xZoomPosistion = 0; xZoomPosistion < xZoom; xZoomPosistion++) {
					
						setPixel(renderPixels[x + y * renderWidth], (x * xZoom) + xPosition + xZoomPosistion, (y * yZoom) + yPosition + yZoomPosistion); 
					}
				}
			}
		}
	}
	
	//Checks if the pixel being rendered is in bounds of the array
	private void setPixel(int pixel, int x, int y) {
		if(x >= camera.x && y >= camera.y && x <= camera.x + camera.w && y <= camera.y + camera.h) {
		
			int pixelIndex = (x - camera.x) + (y - camera.y) * view.getWidth(); 
			if(pixels.length > pixelIndex && pixel != Game.alpha) {
				pixels[pixelIndex] = pixel;
			}
		}
	}
	
}
