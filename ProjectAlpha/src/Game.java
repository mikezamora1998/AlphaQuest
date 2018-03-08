import java.awt.Canvas;
import java.awt.Color;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.lang.Runnable;
import java.lang.Thread;

import javax.swing.JFrame;

import javax.imageio.ImageIO;

import java.io.File;
import java.io.IOException;

public class Game extends JFrame implements Runnable
{
	//alpha color			0xFF FF00DC	
	public static int alpha = 0xFFFF00DC;
	
	private Canvas canvas = new Canvas();
	private RenderHandler renderer;
	private SpriteSheet sheet;
	
	private BufferedImage testImage;
	private Sprite testSprite;
	private Rectangle testRectangle = new Rectangle(960, 540, 100, 100);
	
	private Tiles tiles;
	private Map map;
	
	private GameObject[] objects;
	private KeyBoardListener keyListener = new KeyBoardListener(this);
	private Player player;
	private MouseEventListener mouseListener = new MouseEventListener(this);
	
	//Zooms in on an image
	//1 to 1 ratio
	private int xZoom = 5;
	private int yZoom = 5;
	
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
		
		//prints the file path to assets folder
		System.out.println("GrassTile.png location. = " + Game.class.getResource("assets/GrassTile.png"));
		System.out.println("Tiles.txt location. = " + Game.class.getResource("assets/Tiles.txt"));
		
		BufferedImage sheetImage = loadImage("assets/Tiles1.png");
		
		sheet = new SpriteSheet(sheetImage);
		//size of the blocks in the sprite sheet. (x, y) 16px by 16px default
		sheet.loadSprites(16, 16);
		//retrieves the sprite from a grid (size defined above)
		//testSprite = sheet.getSprite(4, 4);
		
		testImage = loadImage("assets/GrassTile.png");
		//testImage = loadImage("assets/bRODY.jpg");
		
		//testRectangle.generateGraphics(3, 12234);
		
		//Load Tiles
		tiles = new Tiles(new File("bin/assets/Tiles.txt"), sheet);
		
		//Load Map
		map = new Map(new File("bin/assets/Map.txt"), tiles);
		
		//Load Objects
		objects = new GameObject[1];
		player = new Player();
		objects[0] = player;
		
		//Add Listeners
		canvas.addKeyListener(keyListener);
		canvas.addFocusListener(keyListener);
		canvas.addMouseListener(mouseListener);
		canvas.addMouseMotionListener(mouseListener);
	}

	
	public void update() {
		for(int i = 0; i < objects.length; i++) {
			objects[i].update(this);
		}
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

	public void handleCTRL(boolean[] keys) {
		if(keys[KeyEvent.VK_S]) {
			map.saveMap();
		}
	}
	
	public void leftClick(int x, int y) {
		//Divide by tile size default is 16
		System.out.println(x + ", " + y);
		x = (int) Math.floor((x + renderer.getCamera().x)/(16.0 * xZoom));
		y = (int) Math.floor((y + renderer.getCamera().y)/(16.0 * yZoom));
		//(x, y, TileID)
		map.setTile(x, y, 5);
	}
	
	public void rightClick(int x, int y) {
		//Divide by tile size default is 16
		System.out.println(x + ", " + y);
		x = (int) Math.floor((x + renderer.getCamera().x)/(16.0 * xZoom));
		y = (int) Math.floor((y + renderer.getCamera().y)/(16.0 * yZoom));
		map.removeTile(x, y);
	}
	
	public void render() {
			BufferStrategy bufferStrategy = canvas.getBufferStrategy();
			Graphics graphics = bufferStrategy.getDrawGraphics();
			super.paint(graphics);
			
		//renders in linear order. Newest will be rendered over older
			//renders test image
			//renderer.renderImage(testImage, (getWidth()/2) - (testImage.getWidth()/2)*xZoom, (getHeight()/2) - (testImage.getHeight()/2)*yZoom, xZoom, yZoom);
			//renders test sprite from sprite sheet
			//renderer.renderSprite(testSprite, (getWidth()/2) - (testSprite.getWidth()/2)*xZoom, (getHeight()/2) - (testSprite.getHeight()/2)*yZoom, xZoom, yZoom);
			//renders test rectangle
			//renderer.renderRectangle(testRectangle, 1, 1);
			
			map.render(renderer, xZoom, yZoom);
			
			//renders all objects in order of their position in the object array
			for(int i = 0; i < objects.length; i++) {
				objects[i].render(renderer, xZoom, yZoom);
			}
			
			renderer.render(graphics);
			
			graphics.dispose();
			bufferStrategy.show();
			renderer.clear();
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

	public KeyBoardListener getKeyListener() {
		return keyListener;
	}
	
	public MouseEventListener getMouseListener() {
		return mouseListener;
	}
	
	public RenderHandler getRenderer() {
		return renderer;
	}
}
