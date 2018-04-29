import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

/**
 * Handles Rendering functions.
 * Renders pixels to the canvas while accounting for camera and zoom.
 * <p>
 * @see <b>{@literal Constructor: }</b>
 * <p> 
 * {@link #RenderHandler(int width, int height)}
 * <p>
 * <b>{@literal Methods: }</b>
 * <p>
 * {@link #render(Graphics graphics)}
 * <p>
 * {@link #renderImage(BufferedImage image, int xPosition, int yPosition, int xZoom, int yZoom, boolean fixed)}
 * <p>
 * {@link #renderSprite(Sprite sprite, int xPosition, int yPosition, int xZoom, int yZoom, boolean fixed)}
 * <p>
 * {@link #renderRectangle(Rectangle rectangle, int xZoom, int yZoom, boolean fixed)}
 * <p>
 * {@link #renderRectangle(Rectangle rectangle, Rectangle offset, int xZoom, int yZoom, boolean fixed)}
 * <p>
 * {@link #renderArray(int[] renderPixels, int renderWidth, int renderHeight, int xPosition, int yPosition, int xZoom, int yZoom, boolean fixed)}
 * <p>
 * {@link #setPixel(int pixel, int x, int y, boolean fixed)}
 * <p>
 * {@link #Rectangle getCamera()}
 * <p>
 * {@link #clear()}
 * @author Michael, David, Brandon
 */
public class RenderHandler {
	
	private BufferedImage view;
	private Rectangle camera;
	private int[] pixels;
	
	private int maxScreenWidth;
	private int maxScreenHeight;

	public RenderHandler(int width, int height) {
		
//		GraphicsDevice[] graphicsDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		
//		maxScreenWidth = 0;
//		maxScreenHeight = 0;
//		for(int i = 0; i < graphicsDevices.length; i++) {
//			if(maxScreenWidth < graphicsDevices[i].getDisplayMode().getWidth()) {
//				maxScreenWidth = graphicsDevices[i].getDisplayMode().getWidth();
//			}
//			if(maxScreenHeight < graphicsDevices[i].getDisplayMode().getHeight()) {
//				maxScreenHeight = graphicsDevices[i].getDisplayMode().getHeight();
//			}
//		}
		//Create a BufferedImage that will represent our view.
		view = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		//Camera is an object of rectangle class
		camera = new Rectangle(0, 0, width, height);
		//Create an array for pixels
		pixels = ((DataBufferInt) view.getRaster().getDataBuffer()).getData();

	}

	//Render our array of pixels to the screen
	public void render(Graphics graphics){
		//Renders to screen
		graphics.drawImage(view.getSubimage(0, 0, camera.w, camera.h), 0, 0, camera.w, camera.h, null);
	}

	//Render our image to our array of pixels.
	public void renderImage(BufferedImage image, int xPosition, int yPosition, int xZoom, int yZoom, boolean fixed){
		int[] imagePixels = new int[image.getWidth() * image.getHeight()];
		imagePixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), imagePixels, 0, image.getWidth());
		renderArray(imagePixels, image.getWidth(), image.getHeight(), xPosition, yPosition, xZoom, yZoom, fixed);
	}

	public void renderSprite(Sprite sprite, int xPosition, int yPosition, int xZoom, int yZoom, boolean fixed) {
		renderArray(sprite.getPixels(), sprite.getWidth(), sprite.getHeight(), xPosition, yPosition, xZoom, yZoom, fixed);
	}

	public void renderSprite(Sprite sprite, int xPosition, int yPosition, int renderWidth, int renderHeight, int xZoom, int yZoom, boolean fixed, int xOffset, int yOffset) {
		renderArray(sprite.getPixels(), sprite.getWidth(), sprite.getHeight(), renderWidth, renderHeight, xPosition, yPosition, 
					xZoom, yZoom, fixed, xOffset, yOffset);
	}
	
	public void renderRectangle(Rectangle rectangle, int xZoom, int yZoom, boolean fixed){
		int[] rectanglePixels = rectangle.getPixels();
		if(rectanglePixels != null)
			renderArray(rectanglePixels, rectangle.w, rectangle.h, rectangle.x, rectangle.y, xZoom, yZoom, fixed);	
	}
	
	public void renderRectangle(Rectangle rectangle, Rectangle offset, int xZoom, int yZoom, boolean fixed){
		int[] rectanglePixels = rectangle.getPixels();
		if(rectanglePixels != null)
			renderArray(rectanglePixels, rectangle.w, rectangle.h, rectangle.x + offset.x, rectangle.y + offset.y, xZoom, yZoom, fixed);	
	}

	public void renderArray(int[] renderPixels, int renderWidth, int renderHeight, int xPosition, int yPosition, int xZoom, int yZoom, boolean fixed) 
	{
		renderArray(renderPixels, renderWidth, renderHeight, renderWidth, renderHeight, xPosition, yPosition, xZoom, yZoom, fixed, 0, 0);
	}

	/**
		renderPixels = pixels to render
		imageWidth = width of entire image
		imageHeight = height of entire image
		renderWidth = width of image to render
		renderHeight = height of image to render
		xPosition = x position to render image
		yPosition = y position to render image
		xZoom = horizontal zoom
		yZoom = vertical zoom
		fixed = should offset by camera position
		xOffset = offset into the full image to render x
		yOffset = offset into the full image to render y
		
	*/
	public void renderArray(int[] renderPixels, int imageWidth, int imageHeight, int renderWidth, int renderHeight, int xPosition, int yPosition, 
							int xZoom, int yZoom, boolean fixed, int xOffset, int yOffset)
	{
		for(int y = yOffset; y < yOffset + renderHeight; y++)
			for(int x = xOffset; x < xOffset + renderWidth; x++)
				for(int yZoomPosition = 0; yZoomPosition < yZoom; yZoomPosition++)
					for(int xZoomPosition = 0; xZoomPosition < xZoom; xZoomPosition++)
						setPixel(renderPixels[x + y * imageWidth], ((x - xOffset) * xZoom) + xPosition + xZoomPosition, (((y - yOffset) * yZoom) + yPosition + yZoomPosition), fixed);
	}


	private void setPixel(int pixel, int x, int y, boolean fixed) {
		
		int pixelIndex = 0;
		if(!fixed) {
			if(x >= camera.x && y >= camera.y && x <= camera.x + camera.w && y <= camera.y + camera.h){
				pixelIndex = (x - camera.x) + (y - camera.y) * view.getWidth();
			}
		}else {
			if(x >= 0 && y >= 0 && x <= camera.w && y <= camera.h) {
				pixelIndex = x + y * view.getWidth();
			}
		}
		if(pixels.length > pixelIndex && pixel != Game.alpha) {
				pixels[pixelIndex] = pixel;
		}
	}

	public Rectangle getCamera() {
		return camera;
	}
	
	public int getMaxScreenWidth() {
		return maxScreenWidth;
	}
	
	public int getMaxScreenHeight() {
		return maxScreenHeight;
	}

	public void clear(){
		for(int i = 0; i < pixels.length; i++)
			pixels[i] = 0;
	}

}