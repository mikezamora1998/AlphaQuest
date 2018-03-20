import java.awt.Graphics;
import java.awt.Graphics2D;
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
 *  <p>
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

	public RenderHandler(int width, int height) {
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
	public void render(Graphics graphics){
		
		GraphicScaling graphicScale = new GraphicScaling(graphics);
		Graphics2D graphics2D = graphicScale.setGraphics2D();
		
		//Renders to screen
		graphics2D.drawImage(view, 0, 0, view.getWidth(), view.getHeight(), null);

		//graphics.drawString("GRASSSSSS TILLLLLLE!!!!!!!!!!!!!!", 960, 540);
	}

	//Render our image to our array of pixels.
	public void renderImage(BufferedImage image, int xPosition, int yPosition, int xZoom, int yZoom, boolean fixed){
		int[] imagePixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
		renderArray(imagePixels, image.getWidth(), image.getHeight(), xPosition, yPosition, xZoom, yZoom, fixed);
	}

	public void renderSprite(Sprite sprite, int xPosition, int yPosition, int xZoom, int yZoom, boolean fixed) {
		renderArray(sprite.getPixels(), sprite.getWidth(), sprite.getHeight(), xPosition, yPosition, xZoom, yZoom, fixed);
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

	public void renderArray(int[] renderPixels, int renderWidth, int renderHeight, int xPosition, int yPosition, int xZoom, int yZoom, boolean fixed) {
		for(int y = 0; y < renderHeight; y++)
			for(int x = 0; x < renderWidth; x++)
				for(int yZoomPosition = 0; yZoomPosition < yZoom; yZoomPosition++)
					for(int xZoomPosition = 0; xZoomPosition < xZoom; xZoomPosition++)
						setPixel(renderPixels[x + y * renderWidth], (x * xZoom) + xPosition + xZoomPosition, ((y * yZoom) + yPosition + yZoomPosition), fixed);
	}

	private void setPixel(int pixel, int x, int y, boolean fixed) {
		
		int pixelIndex = -1;
		if(!fixed) {
			if(x >= camera.x && y >= camera.y && x <= camera.x + camera.w && y <= camera.y + camera.h){
				pixelIndex = (x - camera.x) + (y - camera.y) * view.getWidth();
			}
		}else {
			if(x >= 0 && y >= 0 && x <= camera.w && y <= camera.h) {
				pixelIndex = x + y * view.getWidth();
			}
		}
		if(pixelIndex > 0) {
			if(pixels.length > pixelIndex && pixel != Game.alpha) {
				pixels[pixelIndex] = pixel;
			}
		}
	}

	public Rectangle getCamera() {
		return camera;
	}

	public void clear(){
		for(int i = 0; i < pixels.length; i++)
			pixels[i] = 0;
	}

}