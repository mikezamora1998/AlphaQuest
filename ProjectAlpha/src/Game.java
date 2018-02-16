import java.awt.Canvas;
import java.awt.Color;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.Graphics;

import java.lang.Runnable;
import java.lang.Thread;

import javax.swing.JFrame;

import javax.imageio.ImageIO;

import java.io.IOException;
//2:19
public class Game extends JFrame implements Runnable
{
	//alpha color			0xFF FF00DC	
	public static int alpha = 0xFFFF00DC;
	
	private Canvas canvas = new Canvas();
	private RenderHandler renderer;
	private BufferedImage testImage;
	private Sprite testSprite;
	private SpriteSheet sheet;
	private Rectangle testRectangle = new Rectangle(960, 540, 100, 100);
	
	public Game() 
	{
		//Make our program shutdown when we exit out.
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Set the position and size of our frame.
		//setBounds(0,0, 1280, 720);
		
		//set the window to full screen and removes tool bar.
		setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
		setUndecorated(true);
	
		//Put our frame in the center of the screen.
		setLocationRelativeTo(null);

		//Add our graphics component
		add(canvas);

		//Make our frame visible.
		setVisible(true);

		//Create our object for buffer strategy.
		canvas.createBufferStrategy(3);

		renderer = new RenderHandler(getWidth(), getHeight());

		System.out.println("GrassTile.png location. = " + Game.class.getResource("assets/GrassTile.png"));
		
		BufferedImage sheetImage = loadImage("assets/Tiles1.png");
		sheet = new SpriteSheet(sheetImage);
		sheet.loadSprites(16, 16);
		
		testImage = loadImage("assets/GrassTile.png");
		testSprite = sheet.getSprite(4, 1);
		
		testRectangle.generateGraphics(3, 12234);
	}

	
	public void update() {

	}
	
	private BufferedImage loadImage(String path) {
		try {
			BufferedImage loadedImage = ImageIO.read(Game.class.getResource(path));
			BufferedImage formattedImage = new BufferedImage(loadedImage.getWidth(), loadedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
			formattedImage.getGraphics().drawImage(loadedImage ,0 ,0 , null);
			
			return formattedImage;
		}catch(IOException exception){
			exception.printStackTrace();
			return null;
		}
	}

	public void render() {
			BufferStrategy bufferStrategy = canvas.getBufferStrategy();
			Graphics graphics = bufferStrategy.getDrawGraphics();
			super.paint(graphics);
			
			//Zooms in on an image
			//1 to 1 ratio
			int xZoom = 55;
			int yZoom = 55;
			//renders in linear order newest will be rendered over older
			//renderer.renderImage(testImage, (getWidth()/2) - (testImage.getWidth()/2)*xZoom, (getHeight()/2) - (testImage.getHeight()/2)*yZoom, xZoom, yZoom);
			renderer.renderSprite(testSprite, (getWidth()/2) - (testImage.getWidth()/2)*xZoom, (getHeight()/2) - (testImage.getHeight()/2)*yZoom, xZoom, yZoom);
			renderer.renderRectangle(testRectangle, 1, 1);
			
			renderer.render(graphics);

			graphics.dispose();
			bufferStrategy.show();
	}

	public void run() {

		long lastTime = System.nanoTime(); //long 2^63
		double nanoSecondConversion = 1000000000.0 / 60; //60 frames per second
		double changeInSeconds = 0;

		while(true) {
			long now = System.nanoTime();

			changeInSeconds += (now - lastTime) / nanoSecondConversion;
			while(changeInSeconds >= 1) {
				update();
				changeInSeconds--;
			}

			render();
			lastTime = now;
		}
	}

	public static void main(String[] args) 
	{
		//Creates "game" object
		Game game = new Game();
		Thread gameThread = new Thread(game);
		gameThread.start();
	}

}
