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

	//Render our array of pixels to the screen
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
				g2.scale(scaleWidth, scaleHeight);
				
				//Renders to screen
				g2.drawImage(view, 0, 0, view.getWidth(), view.getHeight(), null);

				//graphics.drawString("GRASSSSSS TILLLLLLE!!!!!!!!!!!!!!", 960, 540);
	}

	//Render our image to our array of pixels.
	public void renderImage(BufferedImage image, int xPosition, int yPosition, int xZoom, int yZoom)
	{
		int[] imagePixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
		renderArray(imagePixels, image.getWidth(), image.getHeight(), xPosition, yPosition, xZoom, yZoom);
	}

	public void renderSprite(Sprite sprite, int xPosition, int yPosition, int xZoom, int yZoom) {
		renderArray(sprite.getPixels(), sprite.getWidth(), sprite.getHeight(), xPosition, yPosition, xZoom, yZoom);
	}

	public void renderRectangle(Rectangle rectangle, int xZoom, int yZoom)
	{
		int[] rectanglePixels = rectangle.getPixels();
		if(rectanglePixels != null)
			renderArray(rectanglePixels, rectangle.w, rectangle.h, rectangle.x, rectangle.y, xZoom, yZoom);	
	}

	public void renderArray(int[] renderPixels, int renderWidth, int renderHeight, int xPosition, int yPosition, int xZoom, int yZoom) 
	{
		for(int y = 0; y < renderHeight; y++)
			for(int x = 0; x < renderWidth; x++)
				for(int yZoomPosition = 0; yZoomPosition < yZoom; yZoomPosition++)
					for(int xZoomPosition = 0; xZoomPosition < xZoom; xZoomPosition++)
						setPixel(renderPixels[x + y * renderWidth], (x * xZoom) + xPosition + xZoomPosition, ((y * yZoom) + yPosition + yZoomPosition));
	}

	private void setPixel(int pixel, int x, int y) 
	{
		if(x >= camera.x && y >= camera.y && x <= camera.x + camera.w && y <= camera.y + camera.h)
		{
			int pixelIndex = (x - camera.x) + (y - camera.y) * view.getWidth();
			if(pixels.length > pixelIndex && pixel != Game.alpha)
				pixels[pixelIndex] = pixel;
		}
	}

	public Rectangle getCamera() 
	{
		return camera;
	}

	public void clear()
	{
		for(int i = 0; i < pixels.length; i++)
			pixels[i] = 0;
	}

}