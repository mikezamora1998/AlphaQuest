import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;


public class RenderHandler 
{
	private BufferedImage view;
	private int[] pixels;

	public RenderHandler(int width, int height) 
	{
		//Create a BufferedImage that will represent our view.
		view = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		//Create an array for pixels
		pixels = ((DataBufferInt) view.getRaster().getDataBuffer()).getData();
		
		
		/*for(int heightIndex = 0; heightIndex < height; heightIndex++) {
			
			int randomPixel = (int)(Math.random() * 0xFFFFFF);
			
			for(int widthIndex = 0; widthIndex < width; widthIndex++) {
				pixels[heightIndex*width + widthIndex] = randomPixel;
			}
		}*/
	}

	//renders our pixel array t the screen.
	public void render(Graphics graphics)
	{
		/*for(int index = 0; index < pixels.length; index++) {
			pixels[index] = (int)(Math.random() * 0xFFFFFF);
		}*/

		graphics.drawImage(view, 0, 0, view.getWidth(), view.getHeight(), null);
	}
	
	//Render an image to an array
	public void renderImage(BufferedImage image, int xPosition, int yPosition) {
		int[] imagePixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
		for(int y = 0; y < image.getHeight(); y++) {
			for(int x = 0; x < image.getWidth(); x++) {
				pixels[(x + xPosition) + (y + yPosition) * view.getWidth()]  = imagePixels[x + y * image.getWidth()]; 
			}
			
		}
	}

}