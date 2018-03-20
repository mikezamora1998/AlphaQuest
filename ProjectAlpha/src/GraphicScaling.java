import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;

public class GraphicScaling {

	private Graphics graphics;
	private Graphics2D graphics2D;
	private float aspectRatio;
	private int screenHeight;
	private int screenWidth;
	//if true 16 by 9 aspect ratio is kept
	private boolean defaultAspectRatio;
	
	public static final int DEFAULT_HEIGHT = 1080;
	public static final int DEFAULT_WIDTH = 1920;
	
	public GraphicScaling(Graphics graphics) {
		this.graphics = graphics;
	}
	
	public Graphics2D setGraphics2D() {
		//java - get screen size using the Toolkit class
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		// the screen height
		screenSize.getHeight();

		// the screen width
		screenSize.getWidth();

		screenHeight = screenSize.height;
		screenWidth = screenSize.width;

		//Prints screen resolution
		//System.out.println("Screen Height: " + screenHeight + "\nScreen Width: " + screenWidth);

		//scaling to current screen resolution
		float scaleWidth = DEFAULT_HEIGHT / screenHeight;	//screenHeight;
		float scaleHeight = DEFAULT_WIDTH / screenWidth;	//screenWidth;

		//Prints Aspect Ratio
		//System.out.println("Scale Height: " + scaleHeight + "\nScale Width: " + scaleWidth);
				
		//checks if the resolution is equal to 16/9 aspect ratio
		if(scaleWidth == 1 && scaleHeight == 1) {
			
			defaultAspectRatio = true;
			
		} else {
			
			defaultAspectRatio = false;
			
		}
		
		//Prints if default aspect ratio is kept
		System.out.println(defaultAspectRatio);		
		
		//assigns the scaling factor to the smallest ratio
		if(scaleWidth < scaleHeight){
			
			scaleHeight = scaleWidth;
			aspectRatio = scaleWidth;
			
		}else{
			
			scaleWidth = scaleHeight;
			aspectRatio = scaleHeight;
			
		}

		//sets graphics == graphics2D
		graphics2D = (Graphics2D) graphics;

		//scales graphics
		graphics2D.scale(scaleWidth, scaleHeight);		
		
		return graphics2D;
	}
	
	public Graphics getGraphics() {
		return graphics;
	}
	
	public Graphics2D getGraphics2D() {
		return graphics2D;
	}
	
	public int getScreenHeight() {
		return screenHeight;
	}
	
	public int getScreenWidth() {
		return screenWidth;
	}
	
	public float getAspectRatio() {
		return aspectRatio;
	}
	
	public boolean getDefaultAspectRatio() {
		return defaultAspectRatio;
	}
}