import java.awt.Canvas;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.lang.Runnable;
import java.lang.Thread;

import javax.swing.JFrame;

import javax.imageio.ImageIO;

import java.io.File;
import java.io.IOException;

/**
 * Creates a new GameTread that will run the game.
 * <p>
 * @see <b>{@literal Constructor: }</b>
 * <p> 
 * {@link #Game()}
 * <p>
 * <b>{@literal Methods: }</b>
 * <p>
 * {@link #backgroundMusic(String path)}
 * <p>
 * {@link #changeTile(int tileID)}
 * <p>
 * {@link #getKeyListener()}
 * <p>
 * {@link #getMap()}
 * <p>
 * {@link #getMouseListener()}
 * <p>
 * {@link #getRectangleBackground()}
 * <p>
 * {@link #getRenderer()}
 * <p>
 * {@link #getSelectedTile()}
 * <p>
 * {@link #getXZoom()}
 * <p>
 * {@link #getYZoom()}
 * <p>
 * {@link #handleCTRL(boolean[] keys)}
 * <p>
 * {@link #leftClick(int x, int y)}
 * <p>
 * {@link #loadImage(String path)}
 * <p>
 * {@link #loadSprite(String path)}
 * <p>
 * {@link #render()}
 * <p>
 * {@link #rightClick(int x, int y)}
 * <p>
 * {@link #run()}
 * <p>
 * {@link #update()}
 * <p>
 * <b>{@literal Public Variables: }</b>
 * <p>
 * <b>final int</b> {@link #xZoom}
 * <p>
 * <b>final int</b> {@link #yZoom}
 * <p>
 * @author Michael, David, Brandon
 */
@SuppressWarnings("serial")
public class Game extends JFrame implements Runnable {
	//alpha color			0xFF FF00DC	
	public static int alpha = 0xFFFF00DC;
	
	private Canvas canvas = new Canvas();
	private RenderHandler renderer;
	private SpriteSheet sheet;
	private SpriteSheet playerSheet;

	private Tiles tiles;
	private Sprite[] spriteButton;
	private Map map;
	private Rectangle background = new Rectangle(0,0,8000,1500);
	
	private GameObject[] objects;
	private KeyBoardListener keyListener = new KeyBoardListener(this);
	private Player player;
	private MouseEventListener mouseListener = new MouseEventListener(this);
	
	//Zooms in on an image
	public final int xZoom = 3;
	public final int yZoom = 3;
	
	private int TILESIZE = 16;
	private int selectedTileID = 0;
	private int selectedLayer = 0;

	private boolean gameStart;

	private Rectangle startScreenRect = new Rectangle(500,500,500,500);

	private BufferedImage bgLayer1;
	private BufferedImage bgLayer2;
	private BufferedImage bgLayer3;
	private BufferedImage bgLayer4;
	
	public Game() {
		//Make our program shutdown when we exit out.
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		canvas.setBounds(0, 0, 1280, 720);

		//Add our graphics component
		add(canvas);
		pack();
		
		//set the window to full screen and removes tool bar.
		//setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
		//setUndecorated(true);
	
		//Put our frame in the center of the screen.
		setLocationRelativeTo(null);

		//Make our frame visible.
		setVisible(true);

		//Create our object for buffer strategy.
		canvas.createBufferStrategy(2);

		renderer = new RenderHandler(canvas.getWidth(), canvas.getHeight());
		
		//prints the file path to assets folder
		//System.out.println("GrassTile.png location. = " + Game.class.getResource("assets/GrassTile.png"));
		//System.out.println("Tiles.txt location. = " + Game.class.getResource("assets/Tiles.txt"));
		//System.out.println(Game.class.getClassLoader().getResource("").getPath());
		
		bgLayer1 = loadImage("/background C layer1.png");
		bgLayer2 = loadImage("/background C layer4.png");
		
		BufferedImage sheetImage = loadImage("/Tiles1.png");
		
		//size of the blocks in the sprite sheet. (x, y) 16px by 16px default
		sheet = new SpriteSheet(sheetImage);
		sheet.loadSprites(16, 16);
		
		BufferedImage playerSheetImage = loadImage("/Player.png");
		playerSheet = new SpriteSheet(playerSheetImage);
		playerSheet.loadSprites(20, 26);
	
		//background music
		Sound.backGround.play();
		
		//Load Tiles
		tiles = new Tiles(new File("assets/Tiles.txt"), sheet);
		
		//Load Map
		map = new Map(new File("assets/Map.txt"), tiles);
		
		//player animation sprites
		AnimatedSprite playerAnimations = new AnimatedSprite(playerSheet, 5);
		
		//load SDK GUI
		GUIButtons[] buttons = new GUIButtons[tiles.size()];
		Sprite[] tileSprites = tiles.getSprite();
		int guiSpacing = 5;
		for(int i = 0; i < buttons.length; i++) {
			//Rectangle tileRectangle = new Rectangle(0, i * (TILESIZE * xZoom + guiSpacing), TILESIZE * xZoom, TILESIZE * yZoom);
			Rectangle tileRectangle = new Rectangle(0, i * (tileSprites[i].getWidth() * xZoom + guiSpacing), tileSprites[i].getWidth() * xZoom, tileSprites[i].getHeight() * yZoom);
			buttons[i] = new SDKButton(this, i,tileSprites[i], tileRectangle);
		}
		
		GUI gui = new GUI(buttons, 5, 5, true);
		
		GUIButtons[] startButtons = new GUIButtons[tiles.size()];
		for(int i = 0; i < startButtons.length; i++) {
			Rectangle tileRectangle = new Rectangle(100, i * (tileSprites[i].getWidth() * xZoom + guiSpacing), tileSprites[i].getWidth() * xZoom, tileSprites[i].getHeight() * yZoom);
			startButtons[i] = new SDKButton(this, i,tileSprites[i], tileRectangle);
		}
		GUI startButton = new GUI(startButtons, 5, 5, true);

		//Load Objects
		objects = new GameObject[2];
		player = new Player(playerAnimations, xZoom, yZoom);
		objects[0] = player;
		objects[1] = gui;
		//objects[2] = startButton;
		
		//Add Listeners
		canvas.addKeyListener(keyListener);
		canvas.addFocusListener(keyListener);
		canvas.addMouseListener(mouseListener);
		canvas.addMouseMotionListener(mouseListener);
		
		addComponentListener(new ComponentListener() {

			@Override
			public void componentHidden(ComponentEvent e) {}

			@Override
			public void componentMoved(ComponentEvent e) {}

			@Override
			public void componentShown(ComponentEvent e) {}
			
			@Override
			public void componentResized(ComponentEvent e) {
				int newWidth = canvas.getWidth();
				if(newWidth > renderer.getMaxScreenWidth()) {
					newWidth = renderer.getMaxScreenWidth();
				}
				renderer.getCamera().w = newWidth;
				
				int newHeight = canvas.getHeight();
				if(newHeight > renderer.getMaxScreenHeight()) {
					newHeight = renderer.getMaxScreenHeight();
				}
				renderer.getCamera().h = newHeight;
				
				canvas.setSize(newWidth, newHeight);
				pack();
			}
		});
		canvas.requestFocus();
	}

	public void update() {
		for(int i = 0; i < objects.length; i++) {
			objects[i].update(this);
		}
	}
	
	public BufferedImage loadImage(String path) {
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

	public Sprite loadSprite(String path) {
		return new Sprite(loadImage(path));
	}
	
	public void handleCTRL(boolean[] keys) {
		if(keys[KeyEvent.VK_S]) {
			map.saveMap();
		}
	}
	
	public void leftClick(int x, int y) {
		
		Rectangle mouseRectangle = new Rectangle(x, y, 1, 1);
		boolean stopChecking = false;
		
		for(int i = 0; i < objects.length; i++) {
			if(!stopChecking) {
				stopChecking = objects[i].handleMouseClick(mouseRectangle, renderer.getCamera(), xZoom, yZoom);
			}
		}
		
		if(!stopChecking) {
			//Divide by tile size default is 16
			x = (int) Math.floor((x + renderer.getCamera().x)/(16.0 * xZoom));
			y = (int) Math.floor((y + renderer.getCamera().y)/(16.0 * yZoom));
			//(x, y, TileID)
			map.setTile(selectedLayer, x, y, selectedTileID);
		}
	}
	
	public void rightClick(int x, int y) {
		//Divide by tile size default is 16
		System.out.println(x + ", " + y);
		x = (int) Math.floor((x + renderer.getCamera().x)/(16.0 * xZoom));
		y = (int) Math.floor((y + renderer.getCamera().y)/(16.0 * yZoom));
		map.removeTile(selectedLayer, x, y);
	}
	
	public void changeTile(int tileID) {
		selectedTileID = tileID;
	}
	
	public int getSelectedTile() {
		return selectedTileID;
	}
	
	public void render() {
		BufferStrategy bufferStrategy = canvas.getBufferStrategy();
		Graphics graphics = bufferStrategy.getDrawGraphics();
		super.paint(graphics);
		
		//renders in linear order. Newest will be rendered over older
		//renders background images image
		int bgX = 0;
		int bgY = 0;
		renderer.renderImage(bgLayer1, bgX, bgY, 2, 2, true);
		renderer.renderImage(bgLayer2, bgX, bgY, 2, 2, false);
		
		bgX = bgLayer2.getWidth()*2;
		bgY = 0;
		renderer.renderImage(bgLayer2, bgX, bgY, 2, 2, false);
		
		//if(gameStart)
			map.render(renderer, objects, xZoom, yZoom);
		//else
			//renderer.renderRectangle(startScreenRect , 1, 1, true);
		//renderer.renderSprite(animTest, 30, 30, xZoom, yZoom);
		
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

	public static void main(String[] args) {
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
	
	public Map getMap(){
		return map;
	}
	
	public Rectangle getRectangleBackground() {
		return background;
	}
	
	public int getXZoom() {
		return xZoom;
	}
	
	public int getYZoom() {
		return yZoom;
	}
}
